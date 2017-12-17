#include<iostream>
#include<string>
#include<algorithm>

using namespace std;

const int LEN = 2006;

string cmd;
int number;
int nums[LEN];
long long result[LEN];
int a[LEN];
int b[LEN];
int resultLen;
int resultMax;
int resultNum;
int currentMax;
int currentNum;

void dosth1() {
	for (int i = 0; i < number; i++) {
		bool isOk = true;
		int m = i, n = i;
		while ((m - 1 >= 0 && nums[m-1] >= nums[i]) || (n + 1 < number && nums[n+1] >= nums[i])) {
			if (m - 1 >= 0 && nums[m - 1] >= nums[i])
				m--;
			if (n + 1 < number && nums[n + 1] >= nums[i])
				n++;
		}
		for (int j = 0; j < resultLen; j++) {
			if (m == a[j] && n == b[j]) {
				isOk = false;
				break;
			}
		}
		if (isOk) {
			a[resultLen] = m;
			b[resultLen] = n;
			resultLen++;
		}
		result[i] = (n - m + 1) * nums[i];
	}
	sort(result, result + resultLen);
	int k = 0;
	for (int i = resultLen - 1; i >= 0; i--) {
		if (result[i] == result[resultLen - 1])
			k++;
		else 
			break;
	}
	currentMax = result[resultLen - 1];
	currentNum = k;
}

void perm(int p[LEN], int k, int m) {
	if (k == m) {
		dosth1();
	}
	else {
		for (int i = k; i < m; i++) {
			swap(p[k], p[i]);
			perm(p, k + 1, m);
			swap(p[k], p[i]);
		}
	}
}

void dosth2() {
	perm(nums, 0, number);
	if (currentMax > resultMax) {
		resultMax = currentMax;
		resultNum = currentNum;
	}
}

int main() {
	cin >> cmd >> number;
	for (int i = 0; i < number; i++) {
		cin >> nums[i];
	}
	if (cmd == "NORMAL") {
		dosth1();
		cout << currentMax << " " << currentNum;
	}
	else if (cmd == "SORT") {
		dosth2();
		cout << resultMax;
	}
	return 0;
}