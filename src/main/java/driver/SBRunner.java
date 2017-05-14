package driver;

import modules.EditModule;
import modules.PlaybackModule;
import modules.PlaylistModule;
import modules.RecordModule;

import java.io.File;

/**
 * <h2>SBRunner Class</h2>
 * Acts as an underlying catalyst to connect the modules with user interface.
 *
 * @author Shamah M Zoha
 * @version Release Candidate
 */

public class SBRunner {

    private PlaylistModule pl_mod = new PlaylistModule();
    private PlaybackModule pb_mod = new PlaybackModule();
    private RecordModule rec_mod = new RecordModule();
    private EditModule ed_mod = new EditModule();


    /******************************************
     * General Methods
     ******************************************/

    public boolean isPLEmpty() {
        return pl_mod.isEmpty();
    }

    public boolean pbStatus() {
        return pb_mod.pbStatus();
    }


    /******************************************
     * PlaybackModule Methods
     ******************************************/

    public void play() {
        File playFile = pl_mod.fetchFirstFile();
        String pfExtension = playFile.getName().substring((playFile.getName().lastIndexOf('.')) + 1);
        if (pfExtension.equals("mp3")) {
            pb_mod.playMP3(playFile);
        } else {
            pb_mod.playGeneric(playFile);
        }
    }

    public void playSpec(int n) {
        File playFile = pl_mod.fetchSpecFile(n);
        String pfExtension = playFile.getName().substring((playFile.getName().lastIndexOf('.')) + 1);
        if (pfExtension.equals("mp3")) {
            pb_mod.playMP3(playFile);
        } else {
            pb_mod.playGeneric(playFile);
        }
    }

    public void playNext() {
        File nextFile = pl_mod.fetchNextFile();
        String nfExtension = nextFile.getName().substring((nextFile.getName().lastIndexOf('.')) + 1);

        if (nfExtension.equals("mp3")) {
            pb_mod.playMP3(nextFile);
        } else {
            pb_mod.playGeneric(nextFile);
        }
    }

    public void playPrev() {
        File prevFile = pl_mod.fetchPrevFile();
        String pfExtension = prevFile.getName().substring((prevFile.getName().lastIndexOf('.')) + 1);

        if (pfExtension.equals("mp3")) {
            pb_mod.playMP3(prevFile);
        } else {
            pb_mod.playGeneric(prevFile);
        }
    }

    public void playFirst() {
        File firstFile = pl_mod.fetchFirstFile();
        String ffExtension = firstFile.getName().substring((firstFile.getName().lastIndexOf('.')) + 1);

        if (ffExtension.equals("mp3")) {
            pb_mod.playMP3(firstFile);
        } else {
            pb_mod.playGeneric(firstFile);
        }
    }

    public String endTime() {
        return pb_mod.fetchEndTime();
    }

    public String elapsedTime() {
        return pb_mod.fetchElapsedTime();
    }

    public int endTimeSec() {
        return pb_mod.fetchEndTimeSec();
    }

    public int elapsedTimeSec() {
        return pb_mod.fetchElapsedTimeSec();
    }

    public int getClipPosiiton() {
        return pb_mod.getClipPosition();
    }

    public int getClipFrames() {
        return pb_mod.getClipFrames();
    }

    public void pause() {
        pb_mod.pause();
    }

    public void stop() {
        pb_mod.stop();
    }

    public void rewind() {
        pb_mod.pause();
        pb_mod.rewind();
    }

    public void forward() {
        pb_mod.pause();
        pb_mod.forward();
    }

    public void relocate(long newPos) {
        pb_mod.pause();
        pb_mod.relocate(newPos);
    }

    public void clear() {
        pl_mod.clear();
    }

    public void volUp() {
        pb_mod.volumeUp();
    }

    public void volDown() {
        pb_mod.volumeDown();
    }

    public void muteOn() {
        pb_mod.muteOn();
    }

    public void muteOff() {
        pb_mod.muteOff();
    }


    /******************************************
     * PlaylistModule Methods
     ******************************************/

    public void addToPL(File[] addFiles) {
        pl_mod.addMultiFile(addFiles);
    }

    public void removeFromPL(int fileIndx) {
        pl_mod.removeSingleFile(fileIndx);
    }

    public void savePL(File saveFile) {
        pl_mod.saveList(saveFile);
    }

    public void loadPL(File inpFile) {
        pl_mod.loadList(inpFile);
    }

    public String[] fetchPL() {
        return pl_mod.populateList();
    }

    public void shuffleOn() {
        pl_mod.setShuffle(true);
    }

    public void shuffleOff() {
        pl_mod.setShuffle(false);
    }

    public void repeatOneOn() {
        pl_mod.setRepeatOne(true);
    }

    public void repeatOneOff() {
        pl_mod.setRepeatOne(false);
    }

    public void repeatAllOn() {
        pl_mod.setRepeatAll(true);
    }

    public void repeatAllOff() {
        pl_mod.setRepeatAll(false);
    }


    /******************************************
     * RecordModule Methods
     ******************************************/

    public void startRecord() {
        rec_mod.startRecord();
    }

    public void stopRecord() {
        File tmpRecFile = rec_mod.stopRecord();
        pl_mod.addSingleFile(tmpRecFile);
    }

    public void cancelRecord() {
        rec_mod.cancelRecord();
    }

    public void saveRecord(File saveFile) {
        rec_mod.saveRecord(saveFile);
        pl_mod.addSingleFile(saveFile);
    }


    /******************************************
     * EditModule Methods
     ******************************************/

    public void cutAudio(File saveFile, int startPos, int endPos) {
        File inpFile = pl_mod.fetchCurrFile();
        ed_mod.cutAudio(inpFile, saveFile, startPos, endPos);
    }

    public void joinFiles(File joinFile1, File joinFile2, File newFile) {
        ed_mod.joinFiles(joinFile1, joinFile2, newFile);
    }

}
