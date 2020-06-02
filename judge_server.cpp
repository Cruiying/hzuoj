#pragma warning(disable:4996)
#pragma GCC diagnostic ignored "-Wwrite-strings"
#define INT_MAX 2147483647
#define  _CRT_SECURE_NO_WARNINGS
#include "pch.h"
#include <cstdint> 
#include <string.h>
#include <future>
#include <iostream>
#include <limits>
#include <sstream>
#include <string>
#include <thread>
#include <cstring>
#include <stdio.h>
#include <windows.h>
#include <userenv.h>
#include <psapi.h>
#include <tlhelp32.h>
#include <atlbase.h>
#include <ShellAPI.h>
#include "jni_hashmap.h"
#include "com_hqz_hzuoj_judge_system_JudgeSystem.h"

using namespace std;

bool setupIoRedirection(STARTUPINFOW&, std::wstring, std::wstring, HANDLE&, HANDLE&);

const char* getStringToCharArray(string str);

const char* getStringValue(JNIEnv* JniEnv, jstring jStr);

int systemKillProcess(const char* cmd);

string dwordToString(DWORD val);

bool createProcess(const std::wstring&, const std::wstring&, const std::wstring&, HANDLE&, LPVOID, STARTUPINFOW&, PROCESS_INFORMATION&);

DWORD runProcess(PROCESS_INFORMATION&, STARTUPINFOW&, HANDLE& hInput, HANDLE& hOutput, jint, jint, jint&, jint&);

jint getMaxMemoryUsage(PROCESS_INFORMATION&, STARTUPINFOW&, HANDLE& hInput, HANDLE& hOutput, jint);

jint timeoutProcess(PROCESS_INFORMATION&, STARTUPINFOW&, HANDLE& hInput, HANDLE& hOutput, jint);

jint getCurrentMemoryUsage(HANDLE&);

long long getMillisecondsNow();

bool killProcess(PROCESS_INFORMATION&, STARTUPINFOW&, HANDLE& hInput, HANDLE& hOutput);

DWORD getExitCode(HANDLE&);

std::string getErrorMessage(const std::string&);

std::wstring getWideString(const std::string&);

LPWSTR getWideStringPointer(const std::wstring&);

LPCWSTR getConstWideStringPointer(const std::wstring&);
/**
 * JNI�������.
 * ��ȡ�������н��.
 * @param  jenv          - JNI ���л�������
 * @param  jobj   - �Ե���Java�Ķ��������
 * @param  jcmd    - ��ִ�е�������
 * @param  jInputFilePath  - ִ�г���ʱ�������ļ�·��(��ΪNULL)
 * @param  jOutputFilePath - ִ�г���������ļ�·��(��ΪNULL)
 * @param  timeLimit       - ����ִ��ʱ������(ms, 0Ϊ������)
 * @param  memoryLimit     - ����ִ���ڴ�����(KB, 0Ϊ������)
 * @return һ���������н����Map<String, Object>����
 */
JNIEXPORT jobject JNICALL  Java_com_hqz_hzuoj_judge_system_JudgeSystem_runner
(JNIEnv* jenv, jobject jobj, jstring jCmd, jstring jUsername, jstring jPassword, 
	jstring jInputFilePath,jstring jOutputFilePath, jint jTimeLimit, jint jMemoryLimit) {
	const std::wstring        commandLine = getWideString(getStringValue(jenv, jCmd));
	jenv->DeleteLocalRef(jCmd);
	const std::wstring        inputFilePath = getWideString(getStringValue(jenv, jInputFilePath));
	jenv->DeleteLocalRef(jInputFilePath);
	const std::wstring        outputFilePath = getWideString(getStringValue(jenv, jOutputFilePath));
	jenv->DeleteLocalRef(jOutputFilePath);
	const std::wstring username = getWideString(getStringValue(jenv, jUsername));
	jenv->DeleteLocalRef(jUsername);
	const std::wstring password = getWideString(getStringValue(jenv, jPassword));
	jenv->DeleteLocalRef(jPassword);
	HANDLE hInput = { 0 };
	HANDLE hOutput = { 0 };
	HANDLE hToken = { 0 };
	LPVOID lpEnvironment = NULL;
	PROCESS_INFORMATION processInfo = { 0 };
	STARTUPINFOW        startupInfo = { 0 };

	JHashMap            result;
	jint                timeUsage = jTimeLimit;
	jint                memoryUsage = jMemoryLimit;
	DWORD               exitCode = 1;
	
	//�ض���I/O
	if (!setupIoRedirection(startupInfo, inputFilePath, outputFilePath, hInput, hOutput)) {
		//�ض���I/Oʧ��
		CloseHandle(hInput);
		CloseHandle(hOutput);
		//��ȡ����
		result.put("usedTime", timeUsage);
		result.put("usedMemory", memoryUsage);
		result.put("exitCode", exitCode);
		return result.toJObject(jenv);
	}
	//��������
	if (!createProcess(commandLine, username, password, hToken, lpEnvironment, startupInfo, processInfo)) {
		//���̽��̴���ʧ��
		/*�ͷ��ļ����*/
		CloseHandle(hInput);
		CloseHandle(hOutput);
		//��ȡ����
		result.put("usedTime", timeUsage);
		result.put("usedMemory", memoryUsage);
		result.put("exitCode", exitCode);
		return result.toJObject(jenv);
	}
	
	//���н���
	exitCode = runProcess(processInfo, startupInfo, hInput, hOutput, jTimeLimit, jMemoryLimit, timeUsage, memoryUsage);
	/*�ͷ��ļ����*/
	CloseHandle(hInput);
	CloseHandle(hOutput);
	//��ȡ����
	result.put("usedTime", timeUsage);
	result.put("usedMemory", memoryUsage);
	result.put("exitCode", exitCode);

	//��������
	return result.toJObject(jenv);
}

/**
 * �ض����ӽ��̵�I/O.
 * @param startupInfo - STARTUPINFOW�ṹ��
 * @param inputFilePath  - �����ļ�·��
 * @param outputFilePath - ����ļ�·��
 * @param hInput         - �����ļ����
 * @param hOutput        - ����ļ����
 */
bool setupIoRedirection(STARTUPINFOW& startupInfo, std::wstring inputFilePath, std::wstring outputFilePath,HANDLE& hInput, HANDLE& hOutput) {
	SECURITY_ATTRIBUTES sa;
	sa.nLength = sizeof(sa);
	sa.lpSecurityDescriptor = NULL;
	sa.bInheritHandle = TRUE;
	/*�ж��ַ�������Ϊ��*/
	if (!inputFilePath.empty()) {
		/*�����ļ����*/
		hInput = CreateFileW(inputFilePath.c_str(), GENERIC_READ, 0, &sa,OPEN_EXISTING, FILE_ATTRIBUTE_NORMAL, NULL);
		if (hInput == INVALID_HANDLE_VALUE) {
			return false;
		}
	}
	/*�ж��ļ�����Ϊ��*/
	if (!outputFilePath.empty()) {
		/*����ļ��ֲ�*/
		hOutput = CreateFileW(outputFilePath.c_str(), GENERIC_READ | GENERIC_WRITE, FILE_SHARE_READ | FILE_SHARE_WRITE, &sa, CREATE_ALWAYS, FILE_ATTRIBUTE_NORMAL, NULL);
		if (hOutput == INVALID_HANDLE_VALUE) {
			return false;
		}
	}
	startupInfo.cb = sizeof(STARTUPINFOW);
	startupInfo.dwFlags = STARTF_USESHOWWINDOW | STARTF_USESTDHANDLES;
	startupInfo.wShowWindow = SW_HIDE;
	/*�ض����������*/
	startupInfo.hStdInput = hInput;
	/*�ض���������*/
	startupInfo.hStdError = hOutput;
	/*�ض�����̴������*/
	startupInfo.hStdOutput = hOutput;
	return true;
}

/**
 * ��������.
 * @param  commandLine   ��ִ�е�������
 * @return �����Ƿ񴴽��ɹ�
 */
bool createProcess(const std::wstring& commandLine, const std::wstring& username,const std::wstring& password, HANDLE& hToken, LPVOID lpEnvironment, STARTUPINFOW& startupInfo, PROCESS_INFORMATION& processInfo) {
	LPWSTR  lpCommandLine = getWideStringPointer(commandLine);
	//��������
	 bool t = CreateProcess(NULL, lpCommandLine, NULL, NULL, TRUE, CREATE_SUSPENDED, NULL, NULL, &startupInfo, &processInfo);
	 cout << "create process: " << t << endl;
	 return t;
}

/**
 * ���н���.
 * @param  processInfo - ����������Ϣ��PROCESS_INFORMATION�ṹ��
 * @param  timeLimit   - ����ʱʱ������(ms)
 * @param  memoryLimit - ����ʱ�ռ�����(KB)
 * @param  timeUsage   - ����ʱʱ��ռ��(ms)
 * @param  memoryUsage - ����ʱ�ռ�ռ��(ms)
 * @return �����˳�״̬
 */
DWORD runProcess(PROCESS_INFORMATION& processInfo, STARTUPINFOW& startupInfo, HANDLE& hInput, HANDLE& hOutput, jint timeLimit, jint memoryLimit, jint& timeUsage, jint& memoryUsage) {
	/*���ѽ���*/
	ResumeThread(processInfo.hThread);
	/*�첽�����ڴ����*/
	auto f1 = std::async(std::launch::async, getMaxMemoryUsage, ref(processInfo), ref(startupInfo), ref(hInput), ref(hOutput), ref(memoryLimit));
	/*�첽��������ʱ��*/
	auto f2 = std::async(std::launch::async, timeoutProcess, ref(processInfo), ref(startupInfo), ref(hInput), ref(hOutput), ref(timeLimit));
	/*��ʼʱ��*/
	long long startTime = getMillisecondsNow();
	/*�ȴ��������н���*/
	WaitForSingleObject(processInfo.hProcess, timeLimit);
	/*����ʱ��*/
	long long endTime = getMillisecondsNow();
	/*��������ʱ��*/
	timeUsage = f2.get();
	/*��ȡ�ڴ�*/
	memoryUsage = f1.get();
	/*�����Ƿ����н���*/
	if (getExitCode(processInfo.hProcess) == STILL_ACTIVE) {
		/*�رս���*/
		killProcess(processInfo, startupInfo, hInput, hOutput);
	}
	return getExitCode(processInfo.hProcess);
}

/**
 * ��ȡ����ʱ�ڴ�ռ�����ֵ
 * @param  processInfo - ����������Ϣ��PROCESS_INFORMATION�ṹ��
 * @param  memoryLimit - ����ʱ�ռ�����(KB)
 * @return ����ʱ�ڴ�ռ�����ֵ
 */
jint getMaxMemoryUsage(PROCESS_INFORMATION& processInfo, STARTUPINFOW& startupInfo, HANDLE& hInput, HANDLE& hOutput, jint memoryLimit) {
	jint maxMemoryUsage = 0, currentMemoryUsage = 0;
	do {
		/*��ȡ�����ڴ�*/
		currentMemoryUsage = getCurrentMemoryUsage(processInfo.hProcess);
		/*�жϵ�ǰ�ڴ��Ƿ�Ĵ�С*/
		if (currentMemoryUsage > maxMemoryUsage) {
			maxMemoryUsage = currentMemoryUsage;
		}
		/*����10ms*/
		Sleep(10);
		/*�ж��ڴ��Ƿ���*/
		if (memoryLimit != 0 && currentMemoryUsage > memoryLimit) {
			/*�ڴ泬�ޣ�ɱ������*/
			killProcess(processInfo, startupInfo, hInput, hOutput); 
			break;
		}
	} while (getExitCode(processInfo.hProcess) == STILL_ACTIVE);
	return maxMemoryUsage;
}

/*������������ʱ��*/
jint timeoutProcess(PROCESS_INFORMATION& processInfo, STARTUPINFOW& startupInfo, HANDLE& hInput, HANDLE& hOutput, jint timeLimit) {
	/*��ʼʱ��*/
	long long startTime = getMillisecondsNow();
	long long timeUsage = 0;
	do {
		/*����ʱ��*/
		long long endTime = getMillisecondsNow();
		timeUsage = endTime - startTime;
		/*����10ms*/
		Sleep(10);
		if (timeUsage >= timeLimit) {
			/*ʱ�䳬�ޣ�ɱ������*/
			killProcess(processInfo, startupInfo, hInput, hOutput);
			break;
		}
	} while (getExitCode(processInfo.hProcess) == STILL_ACTIVE);
	return timeUsage;
}

/**
 * ��ȡ�����ڴ�ռ�����.
 * @param  hProcess - ���̾��
 * @return ��ǰ�����ڴ�ʹ����(KB)
	*/
jint getCurrentMemoryUsage(HANDLE& hProcess) {
	PROCESS_MEMORY_COUNTERS pmc;
	jint  currentMemoryUsage = 0;
	/*��ȡ�����ڴ�*/
	if (!GetProcessMemoryInfo(hProcess, &pmc, sizeof(pmc))) {
		return 0;
	}
	currentMemoryUsage = pmc.PeakWorkingSetSize >> 10;
	/*����ڴ��ȡʧ��*/
	if (currentMemoryUsage < 0) {
		currentMemoryUsage = INT_MAX;
	}
	return currentMemoryUsage;
}

/**
 * ��ȡ��ǰϵͳʱ��.
 * ����ͳ�Ƴ�������ʱ��.
 * @return ��ǰϵͳʱ��(�Ժ���Ϊ��λ)
 */
long long getMillisecondsNow() {
	return GetTickCount();
}

/**
 * ǿ�����ٽ���.
 * @param  processInfo - ����������Ϣ��PROCESS_INFORMATION�ṹ��
 * @return ���ٽ��̲����Ƿ�ɹ����
 */
bool killProcess(PROCESS_INFORMATION& processInfo, STARTUPINFOW& startupInfo, HANDLE& hInput, HANDLE& hOutput) {
	if (getExitCode(processInfo.hProcess) == STILL_ACTIVE) {
		const string processId = dwordToString(processInfo.dwProcessId);
		const std::string str = "taskkill /pid " + processId + " -t -f";
		const char* cmd = str.c_str();
		
		try{
			if (getExitCode(processInfo.hProcess) == STILL_ACTIVE) {
				DWORD           processId = processInfo.dwProcessId;
				PROCESSENTRY32 processEntry = { 0 };
				processEntry.dwSize = sizeof(PROCESSENTRY32);
				HANDLE handleSnap = CreateToolhelp32Snapshot(TH32CS_SNAPPROCESS, 0);

				if (Process32First(handleSnap, &processEntry)) {
					BOOL isContinue = TRUE;

					do {
						if (processEntry.th32ParentProcessID == processId) {
							HANDLE hChildProcess = OpenProcess(PROCESS_ALL_ACCESS, FALSE, processEntry.th32ProcessID);
							if (hChildProcess) {
								TerminateProcess(hChildProcess, 1);
								CloseHandle(hChildProcess);
							}
						}
						isContinue = Process32Next(handleSnap, &processEntry);
					} while (isContinue);

					HANDLE hBaseProcess = OpenProcess(PROCESS_ALL_ACCESS, FALSE, processId);
					if (hBaseProcess) {
						TerminateProcess(hBaseProcess, 1);
						CloseHandle(hBaseProcess);
					}
				}
			}
		} catch (const std::exception&) {}
		try {
			/*�жϽ����Ƿ��Ѿ�������*/
			if (getExitCode(processInfo.hProcess) == STILL_ACTIVE) {
				/*�ر��߳�*/
				TerminateProcess(processInfo.hThread, 1);
				/*�رս���*/
				TerminateProcess(processInfo.hProcess, 1);
			}
		} catch (const std::exception & e) {}
		try {
			/*�жϽ����Ƿ�ɱ��*/
			if (getExitCode(processInfo.hProcess) == STILL_ACTIVE){
				/*ʹ������system����ǿ�����ٽ���*/
				system(cmd);
			}

		} catch (const std::exception&) {}
		try {
			/*�жϽ����Ƿ�ɱ��*/
			if (getExitCode(processInfo.hProcess) == STILL_ACTIVE) {
				/*ʹ���������ٽ���*/
				systemKillProcess(cmd);
			}
		} catch (const std::exception&) {}
		/*�ͷŽ��̾��*/
		CloseHandle(processInfo.hThread);
		CloseHandle(processInfo.hProcess);
		if (getExitCode(processInfo.hProcess) == STILL_ACTIVE) {
			return false;
		}
		return true;
	}
	return true;
}

/**
 * ִ��ɱ����������
 * @param  ִ������
 * @return ���ٽ��̲����Ƿ�ɹ����
 */
int systemKillProcess(const char* cmd){
	STARTUPINFOA si;
	ZeroMemory(&si, sizeof(si));
	si.cb = sizeof STARTUPINFO;
	PROCESS_INFORMATION pi = { 0 };
	if (CreateProcessA(NULL, (LPSTR)cmd, NULL, NULL, TRUE, NORMAL_PRIORITY_CLASS | CREATE_NO_WINDOW, NULL, NULL, &si, &pi)) {
		WaitForSingleObject(pi.hProcess, INFINITE);
		CloseHandle(pi.hProcess);
		CloseHandle(pi.hThread);
	} else {
		return true;
	}
	return false;
}

/**
 * ��ȡJava��String��ֵ.
 * @param  jniEnv - JNI ���л�������
 * @param  jStr   - ����ȡֵ��Java�ַ���
 * @return Java�ַ�����ֵ
 */
const char* getStringValue(JNIEnv* JniEnv, jstring jStr) {
	if (jStr == NULL) {
		return "";
	}
	const char* cstr = JniEnv->GetStringUTFChars(jStr, 0);
	//�ͷ���Դ
	JniEnv->ReleaseStringUTFChars(jStr, cstr);
	JniEnv->DeleteLocalRef(jStr);
	return cstr;
}

const char* getStringToCharArray(string str) {
	return str.c_str();
}
/*
DWORDתstrning
*/
string dwordToString(DWORD val){
	string cur_str = to_string(long long(val));
	return cur_str;
}
/**
 * ��ȡӦ�ó����˳�״̬.
 * 0��ʾ�����˳�, 259��ʾ��������.
 * @param  hProcess - ���̵ľ��
 * @return �˳�״̬
 */
DWORD getExitCode(HANDLE& hProcess) {
	DWORD exitCode = 0;
	GetExitCodeProcess(hProcess, &exitCode);
	return exitCode;
}


/**
 * ��ȡstd::wstring���͵��ַ���.
 * @param  str - std::string���͵��ַ���
 * @return wstring���͵��ַ���
 */
std::wstring getWideString(const std::string& str) {
	std::wstring wstr(str.begin(), str.end());
	return wstr;
}

/**
 * ��ȡstd::wstring��ӦLPWSTR���͵�ָ��.
 * @param  str - std::wstring���͵��ַ���
 * @return ��ӦLPWSTR���͵�ָ��
 */
LPWSTR getWideStringPointer(const std::wstring& str) {
	return const_cast<LPWSTR>(str.c_str());
}

/**
 * ��ȡstd::wstring��ӦLPCWSTR���͵�ָ��.
 * @param  str - std::wstring���͵��ַ���
 * @return ��ӦLPCWSTR���͵�ָ��
 */
LPCWSTR getConstWideStringPointer(const std::wstring& str) {
	return str.c_str();
}
