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
 * JNI调用入口.
 * 获取程序运行结果.
 * @param  jenv          - JNI 运行环境引用
 * @param  jobj   - 对调用Java的对象的引用
 * @param  jcmd    - 待执行的命令行
 * @param  jInputFilePath  - 执行程序时的输入文件路径(可为NULL)
 * @param  jOutputFilePath - 执行程序后的输出文件路径(可为NULL)
 * @param  timeLimit       - 程序执行时间限制(ms, 0为不限制)
 * @param  memoryLimit     - 程序执行内存限制(KB, 0为不限制)
 * @return 一个包含运行结果的Map<String, Object>对象
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
	
	//重定向I/O
	if (!setupIoRedirection(startupInfo, inputFilePath, outputFilePath, hInput, hOutput)) {
		//重定向I/O失败
		CloseHandle(hInput);
		CloseHandle(hOutput);
		//获取数据
		result.put("usedTime", timeUsage);
		result.put("usedMemory", memoryUsage);
		result.put("exitCode", exitCode);
		return result.toJObject(jenv);
	}
	//创建进程
	if (!createProcess(commandLine, username, password, hToken, lpEnvironment, startupInfo, processInfo)) {
		//进程进程创建失败
		/*释放文件句柄*/
		CloseHandle(hInput);
		CloseHandle(hOutput);
		//获取数据
		result.put("usedTime", timeUsage);
		result.put("usedMemory", memoryUsage);
		result.put("exitCode", exitCode);
		return result.toJObject(jenv);
	}
	
	//运行进程
	exitCode = runProcess(processInfo, startupInfo, hInput, hOutput, jTimeLimit, jMemoryLimit, timeUsage, memoryUsage);
	/*释放文件句柄*/
	CloseHandle(hInput);
	CloseHandle(hOutput);
	//获取数据
	result.put("usedTime", timeUsage);
	result.put("usedMemory", memoryUsage);
	result.put("exitCode", exitCode);

	//返回数据
	return result.toJObject(jenv);
}

/**
 * 重定向子进程的I/O.
 * @param startupInfo - STARTUPINFOW结构体
 * @param inputFilePath  - 输入文件路径
 * @param outputFilePath - 输出文件路径
 * @param hInput         - 输入文件句柄
 * @param hOutput        - 输出文件句柄
 */
bool setupIoRedirection(STARTUPINFOW& startupInfo, std::wstring inputFilePath, std::wstring outputFilePath,HANDLE& hInput, HANDLE& hOutput) {
	SECURITY_ATTRIBUTES sa;
	sa.nLength = sizeof(sa);
	sa.lpSecurityDescriptor = NULL;
	sa.bInheritHandle = TRUE;
	/*判断字符串不能为空*/
	if (!inputFilePath.empty()) {
		/*输入文件句柄*/
		hInput = CreateFileW(inputFilePath.c_str(), GENERIC_READ, 0, &sa,OPEN_EXISTING, FILE_ATTRIBUTE_NORMAL, NULL);
		if (hInput == INVALID_HANDLE_VALUE) {
			return false;
		}
	}
	/*判断文件不能为空*/
	if (!outputFilePath.empty()) {
		/*输出文件局部*/
		hOutput = CreateFileW(outputFilePath.c_str(), GENERIC_READ | GENERIC_WRITE, FILE_SHARE_READ | FILE_SHARE_WRITE, &sa, CREATE_ALWAYS, FILE_ATTRIBUTE_NORMAL, NULL);
		if (hOutput == INVALID_HANDLE_VALUE) {
			return false;
		}
	}
	startupInfo.cb = sizeof(STARTUPINFOW);
	startupInfo.dwFlags = STARTF_USESHOWWINDOW | STARTF_USESTDHANDLES;
	startupInfo.wShowWindow = SW_HIDE;
	/*重定向进程输入*/
	startupInfo.hStdInput = hInput;
	/*重定向进程输出*/
	startupInfo.hStdError = hOutput;
	/*重定向进程错误输出*/
	startupInfo.hStdOutput = hOutput;
	return true;
}

/**
 * 创建进程.
 * @param  commandLine   待执行的命令行
 * @return 进程是否创建成功
 */
bool createProcess(const std::wstring& commandLine, const std::wstring& username,const std::wstring& password, HANDLE& hToken, LPVOID lpEnvironment, STARTUPINFOW& startupInfo, PROCESS_INFORMATION& processInfo) {
	LPWSTR  lpCommandLine = getWideStringPointer(commandLine);
	//创建进程
	 bool t = CreateProcess(NULL, lpCommandLine, NULL, NULL, TRUE, CREATE_SUSPENDED, NULL, NULL, &startupInfo, &processInfo);
	 cout << "create process: " << t << endl;
	 return t;
}

/**
 * 运行进程.
 * @param  processInfo - 包含进程信息的PROCESS_INFORMATION结构体
 * @param  timeLimit   - 运行时时间限制(ms)
 * @param  memoryLimit - 运行时空间限制(KB)
 * @param  timeUsage   - 运行时时间占用(ms)
 * @param  memoryUsage - 运行时空间占用(ms)
 * @return 进程退出状态
 */
DWORD runProcess(PROCESS_INFORMATION& processInfo, STARTUPINFOW& startupInfo, HANDLE& hInput, HANDLE& hOutput, jint timeLimit, jint memoryLimit, jint& timeUsage, jint& memoryUsage) {
	/*唤醒进程*/
	ResumeThread(processInfo.hThread);
	/*异步监听内存情况*/
	auto f1 = std::async(std::launch::async, getMaxMemoryUsage, ref(processInfo), ref(startupInfo), ref(hInput), ref(hOutput), ref(memoryLimit));
	/*异步监听运行时间*/
	auto f2 = std::async(std::launch::async, timeoutProcess, ref(processInfo), ref(startupInfo), ref(hInput), ref(hOutput), ref(timeLimit));
	/*开始时间*/
	long long startTime = getMillisecondsNow();
	/*等待进程运行结束*/
	WaitForSingleObject(processInfo.hProcess, timeLimit);
	/*结束时间*/
	long long endTime = getMillisecondsNow();
	/*计算运行时间*/
	timeUsage = f2.get();
	/*获取内存*/
	memoryUsage = f1.get();
	/*进程是否运行结束*/
	if (getExitCode(processInfo.hProcess) == STILL_ACTIVE) {
		/*关闭进程*/
		killProcess(processInfo, startupInfo, hInput, hOutput);
	}
	return getExitCode(processInfo.hProcess);
}

/**
 * 获取运行时内存占用最大值
 * @param  processInfo - 包含进程信息的PROCESS_INFORMATION结构体
 * @param  memoryLimit - 运行时空间限制(KB)
 * @return 运行时内存占用最大值
 */
jint getMaxMemoryUsage(PROCESS_INFORMATION& processInfo, STARTUPINFOW& startupInfo, HANDLE& hInput, HANDLE& hOutput, jint memoryLimit) {
	jint maxMemoryUsage = 0, currentMemoryUsage = 0;
	do {
		/*获取进程内存*/
		currentMemoryUsage = getCurrentMemoryUsage(processInfo.hProcess);
		/*判断当前内存是否的大小*/
		if (currentMemoryUsage > maxMemoryUsage) {
			maxMemoryUsage = currentMemoryUsage;
		}
		/*休眠10ms*/
		Sleep(10);
		/*判断内存是否超限*/
		if (memoryLimit != 0 && currentMemoryUsage > memoryLimit) {
			/*内存超限，杀死进程*/
			killProcess(processInfo, startupInfo, hInput, hOutput); 
			break;
		}
	} while (getExitCode(processInfo.hProcess) == STILL_ACTIVE);
	return maxMemoryUsage;
}

/*监听程序运行时间*/
jint timeoutProcess(PROCESS_INFORMATION& processInfo, STARTUPINFOW& startupInfo, HANDLE& hInput, HANDLE& hOutput, jint timeLimit) {
	/*开始时间*/
	long long startTime = getMillisecondsNow();
	long long timeUsage = 0;
	do {
		/*结束时间*/
		long long endTime = getMillisecondsNow();
		timeUsage = endTime - startTime;
		/*休眠10ms*/
		Sleep(10);
		if (timeUsage >= timeLimit) {
			/*时间超限，杀死进程*/
			killProcess(processInfo, startupInfo, hInput, hOutput);
			break;
		}
	} while (getExitCode(processInfo.hProcess) == STILL_ACTIVE);
	return timeUsage;
}

/**
 * 获取进程内存占用情况.
 * @param  hProcess - 进程句柄
 * @return 当前物理内存使用量(KB)
	*/
jint getCurrentMemoryUsage(HANDLE& hProcess) {
	PROCESS_MEMORY_COUNTERS pmc;
	jint  currentMemoryUsage = 0;
	/*获取进程内存*/
	if (!GetProcessMemoryInfo(hProcess, &pmc, sizeof(pmc))) {
		return 0;
	}
	currentMemoryUsage = pmc.PeakWorkingSetSize >> 10;
	/*如果内存获取失败*/
	if (currentMemoryUsage < 0) {
		currentMemoryUsage = INT_MAX;
	}
	return currentMemoryUsage;
}

/**
 * 获取当前系统时间.
 * 用于统计程序运行时间.
 * @return 当前系统时间(以毫秒为单位)
 */
long long getMillisecondsNow() {
	return GetTickCount();
}

/**
 * 强制销毁进程.
 * @param  processInfo - 包含进程信息的PROCESS_INFORMATION结构体
 * @return 销毁进程操作是否成功完成
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
			/*判断进程是否已经被销毁*/
			if (getExitCode(processInfo.hProcess) == STILL_ACTIVE) {
				/*关闭线程*/
				TerminateProcess(processInfo.hThread, 1);
				/*关闭进程*/
				TerminateProcess(processInfo.hProcess, 1);
			}
		} catch (const std::exception & e) {}
		try {
			/*判断进程是否杀死*/
			if (getExitCode(processInfo.hProcess) == STILL_ACTIVE){
				/*使用命令system命令强制销毁进程*/
				system(cmd);
			}

		} catch (const std::exception&) {}
		try {
			/*判断进程是否杀死*/
			if (getExitCode(processInfo.hProcess) == STILL_ACTIVE) {
				/*使用命令销毁进程*/
				systemKillProcess(cmd);
			}
		} catch (const std::exception&) {}
		/*释放进程句柄*/
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
 * 执行杀死进程命令
 * @param  执行命令
 * @return 销毁进程操作是否成功完成
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
 * 获取Java中String的值.
 * @param  jniEnv - JNI 运行环境引用
 * @param  jStr   - 待获取值的Java字符串
 * @return Java字符串的值
 */
const char* getStringValue(JNIEnv* JniEnv, jstring jStr) {
	if (jStr == NULL) {
		return "";
	}
	const char* cstr = JniEnv->GetStringUTFChars(jStr, 0);
	//释放资源
	JniEnv->ReleaseStringUTFChars(jStr, cstr);
	JniEnv->DeleteLocalRef(jStr);
	return cstr;
}

const char* getStringToCharArray(string str) {
	return str.c_str();
}
/*
DWORD转strning
*/
string dwordToString(DWORD val){
	string cur_str = to_string(long long(val));
	return cur_str;
}
/**
 * 获取应用程序退出状态.
 * 0表示正常退出, 259表示仍在运行.
 * @param  hProcess - 进程的句柄
 * @return 退出状态
 */
DWORD getExitCode(HANDLE& hProcess) {
	DWORD exitCode = 0;
	GetExitCodeProcess(hProcess, &exitCode);
	return exitCode;
}


/**
 * 获取std::wstring类型的字符串.
 * @param  str - std::string类型的字符串
 * @return wstring类型的字符串
 */
std::wstring getWideString(const std::string& str) {
	std::wstring wstr(str.begin(), str.end());
	return wstr;
}

/**
 * 获取std::wstring对应LPWSTR类型的指针.
 * @param  str - std::wstring类型的字符串
 * @return 对应LPWSTR类型的指针
 */
LPWSTR getWideStringPointer(const std::wstring& str) {
	return const_cast<LPWSTR>(str.c_str());
}

/**
 * 获取std::wstring对应LPCWSTR类型的指针.
 * @param  str - std::wstring类型的字符串
 * @return 对应LPCWSTR类型的指针
 */
LPCWSTR getConstWideStringPointer(const std::wstring& str) {
	return str.c_str();
}
