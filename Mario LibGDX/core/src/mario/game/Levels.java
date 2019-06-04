package mario.game;


public class Levels {

	private int			levelCount;
	private boolean[]	levelComplete;
	private boolean[]	levelUnlocked;

	public Levels(int count) {
		levelCount = count;
		levelComplete = new boolean[levelCount];
		levelUnlocked = new boolean[levelCount];
		for (int i = 0; i < levelCount; i++) {
			levelComplete[i] = false;
			levelUnlocked[i] = false;
		}
		levelUnlocked[0] = true;							//Set first level to be unlocked by default
		levelUnlocked[1] = true;
	}

	public void complete(int level) {
		levelComplete[level - 1] = true;
	}

	public void unlock(int level) {
		levelUnlocked[level - 1] = true;
	}

	public boolean isUnlocked(int level) {
		return levelUnlocked[level - 1];
	}
}
