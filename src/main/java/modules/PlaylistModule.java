package modules;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * <h1>PlayList Module</h1><br>
 * Designed for maintaining a play list in SoundByte.<br>
 * Also provides a backbone for file management in the software.<br><br>
 * <p>
 * Whenever a valid sound file is loaded into the app, it is added to the play list
 * before being played, such that the file could be played repeatedly instead of just once.
 * Furthermore, generalizing of cases can be done through the use of play list, such that both
 * single files and multiple files can be treated in the same way.<br><br>
 * <p>
 * Also, play lists can be saved and loaded using this module, with options of saving them in native
 * extension of '.sbp' or the generalized form of '.txt', where only the absolute path of the files are
 * stored, so that they can be loaded upon loading the play list file.<br><br>
 *
 * @author Shamah M Zoha
 * @version Release Candidate
 */

public class PlaylistModule {
    private LinkedList<File> playlistContainer;
    private int playlistSize, prevIndx, nextIndx;
    private boolean shuffle, repeatOne, repeatAll;


    /**
     * Default constructor for PlaylistModule.<br>
     * Initializes the playlistContainer and playlistSize variables for use.<br>
     * Also, sets the shuffle and repeat flags to false, such that they remain disabled
     * when the PlaylistModule object is created.
     */

    public PlaylistModule() {
        playlistContainer = new LinkedList<File>();
        playlistSize = 0;
        prevIndx = nextIndx = 0;
        shuffle = repeatOne = repeatAll = false;
    }


    /**
     * <h2> Size Method</h2><br>
     * Used in other modules and driver class to obtain the private variable playlistSize.<br><br>
     *
     * @return The size of the playlistSize.<br>
     */

    public int size() {
        return playlistSize;
    }


    /**
     * <h2>isEmpty Method</h2><br>
     *
     * @return A boolean value to point out if the play list is empty or not.<br>
     * If playlistSize is 0, returns true; false otherwise.
     */

    public boolean isEmpty() {
        if (playlistSize == 0) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * <h2> isShuffle Method</h2><br>
     *
     * @return A boolean value to point out if shuffle is toggled or not.<br>
     * Returns either true or false, depending on the scenario.
     */
    public boolean isShuffle() {
        return shuffle;
    }


    /**
     * <h2> isRepeatOne Method</h2><br>
     *
     * @return A boolean value to point out if Repeat One is toggled or not.<br>
     * Returns either true or false, depending on the scenario.
     */

    public boolean isRepeatOne() {
        return repeatOne;
    }


    /**
     * <h2> isRepeatAll Method</h2><br>
     *
     * @return A boolean value to point out if Repeat All is toggled or not.<br>
     * Returns either true or false, depending on the scenario.
     */

    public boolean isRepeatAll() {
        return repeatAll;
    }


    /**
     * <h2> setShuffle Method</h2><br>
     * Used in other modules and driver class to alter the value of the private variable shuffle.<br><br>
     *
     * @param shuffVal The new value that is to replace the existing of the shuffle variable.
     */

    public void setShuffle(boolean shuffVal) {
        shuffle = shuffVal;
    }


    /**
     * <h2> setRepeatOne Method</h2><br>
     * Used in other modules and driver class to alter the value of the private variable repeatOne.<br><br>
     *
     * @param repOneVal The new value that is to replace the existing of the repeatOne variable.
     */

    public void setRepeatOne(boolean repOneVal) {
        repeatOne = repOneVal;
    }


    /**
     * <h2> setRepeatAll Method</h2><br>
     * Used in other modules and driver class to alter the value of the private variable repeatAll.<br><br>
     *
     * @param repAllVal The new value that is to replace the existing of the repeatAll variable.
     */

    public void setRepeatAll(boolean repAllVal) {
        repeatAll = repAllVal;
    }


    /**
     * <h2> addSingleFile Method</h2><br>
     * This helps in adding a single file to the play list. Helps the loadList method.<br><br>
     *
     * @param newFile The file to be added to the play list.
     */

    public void addSingleFile(File newFile) {
        playlistContainer.add(newFile);
        playlistSize++;
    }


    /**
     * <h2> addMultiFile Method</h2><br>
     * This helps in adding multiple file to the play list. It is primarily used in the UI
     * to enable selection and loading of multiple files at one time.<br><br>
     *
     * @param newFiles The array of Files to be added to the play list.
     */

    public void addMultiFile(File[] newFiles) {
        for (int i = 0; i < newFiles.length; i++) {
            playlistContainer.add(newFiles[i]);
            playlistSize++;
        }
    }


    /**
     * <h2> fetchNextFile Method</h2><br>
     * This helps in fetching the next file that is on the play list, depending on
     * the selection mode that is enabled, i.e. shuffle, repeat one and repeat all.<br>
     * The file returned is manipulated by the Main class, sending it to the PlaybackModule module
     * for the music to be played.<br><br>
     *
     * @return The file that is next in line to be played, depending on the selection algorithms
     * enabled.
     */

    public File fetchNextFile() {
        prevIndx = nextIndx;

        if (isShuffle()) {
            while (prevIndx == nextIndx) {
                nextIndx = (int) (Math.random() * playlistSize);
            }
        } else if (isRepeatAll()) {
            nextIndx++;

            if (nextIndx >= playlistSize) {
                nextIndx = 0;
            }
        } else if (isRepeatOne()) {
            prevIndx = nextIndx;
        } else {
            nextIndx++;

            if (nextIndx >= playlistSize) {
                nextIndx = prevIndx;
            }
        }

        return playlistContainer.get(nextIndx);
    }


    /**
     * <h2> fetchFirstFile Method</h2><br>
     * This helps in fetching the first file that is on the play list, regardless
     * the selection algorithm that has been enabled.<br>
     * This helps in attaining the first file in the play list, thus ensuring
     * starting point, where from the application would start playing music.<br>
     * The file returned is manipulated by the Main class, sending it to the PlaybackModule module
     * for the music to be played.<br><br>
     *
     * @return The file that is next in line to be played, depending on the selection algorithms
     * enabled.
     */

    public File fetchFirstFile() {
        prevIndx = nextIndx = 0;
        return playlistContainer.get(0);
    }


    /**
     * <h2> fetchSpecFile Method</h2><br>
     * This helps in fetching the file depending on the index that has been provided by the user.<br>
     * This allows the files that are displayed in the UI (on the JList) to be played with just a double
     * click.<br><br>
     *
     * @param n The index of the file that has been selected to be played.
     * @return The file that is stored on the given index.
     */

    public File fetchSpecFile(int n) {
        prevIndx = nextIndx;
        nextIndx = n;
        return playlistContainer.get(nextIndx);
    }


    /**
     * @return
     */

    public File fetchCurrFile() {
        return playlistContainer.get(nextIndx);
    }

    /**
     * <h2> fetchPrevFile Method</h2><br>
     * This helps in fetching the previous file that had been played, depending on
     * the selection mode that is enabled, i.e. shuffle, repeat one and repeat all.<br>
     * The file returned is manipulated by the Main class, sending it to the PlaybackModule module
     * for the music to be played.<br><br>
     *
     * @return The file that had been played before, depending on the selection algorithms
     * enabled.
     */

    public File fetchPrevFile() {
        if (isShuffle()) {
            return fetchNextFile();
        } else if (isRepeatOne()) {
            return playlistContainer.get(nextIndx);
        } else {
            nextIndx = prevIndx;
            prevIndx--;


            if (prevIndx < 0) {
                prevIndx = 0;
            }

            return playlistContainer.get(nextIndx);
        }
    }


    /**
     * <h2> removeSingleFile Method</h2><br>
     * This helps in removing a single file from the play list.<br><br>
     *
     * @param newFile The file to be added to the play list.
     */

    public void removeSingleFile(int n) {
        playlistContainer.remove(n);
        playlistSize--;
    }


    /**
     * <h2> clear Method</h2><br>
     * Clears and resets the whole play list upon call.
     */

    public void clear() {
        playlistContainer.clear();
        playlistSize = 0;
    }


    /**
     * <h2> loadList Method</h2><br>
     * Opens the SoundByte PlaylistModule (.sbp) files and loads it to the current play list.<br>
     * The sbp file is a mere list of absolute paths of the files and it is written using the
     * saveList method in the same module.<br>
     * The addSingleFile method helps in achieving the addition of the files to the current playlist.<br>
     * <br>
     *
     * @param plInFile The sbp file that is to be appended to the list.
     */

    public void loadList(File plInFile) {
        try {
            Scanner fileReader = new Scanner(plInFile);

            while (fileReader.hasNextLine()) {
                addSingleFile(new File(fileReader.nextLine()));
                playlistSize++;
            }

            fileReader.close();

        } catch (IOException ioe) {
            System.err.println(ioe.getLocalizedMessage());
        }
    }


    /**
     * <h2>saveList Method</h2><br>
     * Writes out the absolute paths of the files that are currently stored in the play list
     * to a spb file that is created using the UI.<br>
     *
     * @param plOutFile The absolute path of the saved file provided by the UI.
     */

    public void saveList(File plOutFile) {
        try {
            FileWriter plWriter = new FileWriter(plOutFile);
            String outString = "";

            for (Iterator<File> i = playlistContainer.iterator(); i.hasNext(); ) {
                outString = outString + i.next().getAbsolutePath() + "\n";
            }

            plWriter.write(outString);
            plWriter.close();

        } catch (IOException ioe) {
            System.err.println(ioe.getLocalizedMessage());
        }
    }


    /**
     * <h2>showList Method</h2><br>
     * Returns a string that bears the names of all the files that are currently in
     * the play list. Helps in debugging from the Java console.<br>
     * <br>
     *
     * @return The names of the files stored in the play list in an ordered fashion.
     */

    public String showList() {
        String retString = "";
        int count = 1;

        for (Iterator<File> i = playlistContainer.iterator(); i.hasNext(); ) {
            retString = retString + count + " : " + (i.next().getName() + "\n");
            count++;
        }

        return retString;
    }

    /**
     * <h2>populateList Method</h2><br>
     * Returns a string array that contains the names of the files that are
     * stored in the playlistContainer. Helps in populating the JList in the UI.<br>
     * <br>
     *
     * @return A string array containing the names of the files.
     */

    public String[] populateList() {
        String[] retArr = new String[size()];
        int indx = 0;

        for (Iterator<File> i = playlistContainer.iterator(); i.hasNext(); ) {
            retArr[indx] = i.next().getName();
            indx++;
        }

        return retArr;
    }


}
