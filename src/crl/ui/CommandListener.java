package crl.ui;

public interface CommandListener {
	int QUIT = 0;
	int SAVE = 1;
	int NONE = 2;
	int HELP = 3;
	int LOOK = 4;
	int RESTART = 5;
	int SHOWINVEN = 6;
	int SHOWHISCORES = 7;
	int SHOWSKILLS = 8;
	int SHOWSTATS = 9;
	int PROMPTQUIT = 10;
	int PROMPTSAVE = 11;
	int SHOWUNEQUIP = 12;
	int SHOWMESSAGEHISTORY = 13;
	int SHOWMAP = 14;
	int SWITCHMUSIC = 15;
	int EXAMINELEVELMAP = 16;
	int CHARDUMP = 17;

	void commandSelected(int pCommand);
}