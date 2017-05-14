package modules;

import javax.sound.sampled.*;
import java.io.File;

/**
 * <h2>PlaybackModule Module</h2>
 * Provides the options of playing audio from a file, as well as control the volume functions.
 *
 * @author Shamah M Zoha
 * @version Release Candidate
 */

public class PlaybackModule {
    private AudioInputStream ais;
    private Clip playableClip;
    private static BooleanControl muteController;
    private static FloatControl volController;
    private static boolean mute = false;
    private static float volumeLevel = 0.0f;

    /**
     * <h2>isMute Method</h2>
     * Returns true or false depending on the value held by the mute variable.<br>
     *
     * @return The value of the mute variable.
     */

    public boolean isMute() {
        return mute;
    }


    /**
     * <h2>getVolLevel Method</h2>
     * Returns the value held by the volumeLevel variable.<br>
     *
     * @return The value of the volumeLevel variable.
     */

    public float getVolLevel() {
        return volumeLevel;
    }


    /**
     * <h2>pbStatus Method</h2>
     * Returns the status of the playableClip, i.e. if it is running or not.
     * Helps in the driver module and the UI.<br>
     *
     * @return A boolean value based on the status of the Clip object.
     */

    public boolean pbStatus() {
        if (playableClip != null) {
            return playableClip.isActive();
        } else {
            return false;
        }
    }


    /**
     * <h2>playGeneric Method</h2>
     * This is the basic method for playing files through Clip Object.<br>
     * First, it is checked if the clip is being paused, (i.e. by checking the input stream)
     * if it is, it is started from that very same position.<br>
     * Otherwise, an audio input stream is obtained from the provided file, and a clip is created and opened using it.<br>
     * The mute controller and the volume controller is then instantiated for the clip, and they are assigned values
     * as stored in the variables, to ensure concurrency.<br>
     * Finally, the clip is started, resulting the playback of files.
     *
     * @param genFile The file that is to be played.
     */

    public void playGeneric(File genFile) {
        try {
            if (ais != null) {
                playableClip.start();
            } else {
                ais = AudioSystem.getAudioInputStream(genFile);
                playableClip = AudioSystem.getClip();
                playableClip.open(ais);

                muteController = (BooleanControl) playableClip.getControl(BooleanControl.Type.MUTE);
                volController = (FloatControl) playableClip.getControl(FloatControl.Type.MASTER_GAIN);

                if (isMute()) {
                    muteOn();
                } else {
                    muteOff();
                }

                if (getVolLevel() != 0.0f) {
                    volController.setValue(volumeLevel);
                }

                playableClip.start();
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }


    /**
     * <h2>playMP3 Method</h2>
     * This is a special method that is used to play MP3 files. The special case is used because MP3 files
     * need to be encoded with the help of MP3Plugin from Oracle in order to be played. (Plugin added into the library).<br>
     * First, it is checked if the clip is being paused, (i.e. by checking the input stream)
     * if it is, it is started from that very same position.<br>
     * If not, a temporary audio input stream is created to obtain the stream from the file, from which it's format is derived.<br>
     * A decoded format is then created based on the format obtained, and the original audio input stream is then instantiated
     * accordingly, with the decoded format and the temporary stream.<br>
     * The Clip object is then instantiated with the help of the stream, and after setting the options for mute and volume,
     * it is started and the music is played.<br>
     *
     * @param mp3File The MP3 file that is to be played.
     */

    public void playMP3(File mp3File) {
        try {
            if (ais != null) {
                playableClip.start();
            } else {
                AudioInputStream tmpAis = AudioSystem.getAudioInputStream(mp3File);
                AudioFormat orgFormat = tmpAis.getFormat();
                AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                        orgFormat.getSampleRate(), 16, orgFormat.getChannels(),
                        orgFormat.getChannels() * 2, orgFormat.getSampleRate(),
                        false);
                ais = AudioSystem.getAudioInputStream(decodedFormat, tmpAis);
                playableClip = AudioSystem.getClip();
                playableClip.open(ais);


                muteController = (BooleanControl) playableClip.getControl(BooleanControl.Type.MUTE);
                volController = (FloatControl) playableClip.getControl(FloatControl.Type.MASTER_GAIN);

                if (isMute()) {
                    muteOn();
                } else {
                    muteOff();
                }

                if (getVolLevel() != 0.0f) {
                    volController.setValue(volumeLevel);
                }

                playableClip.start();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * <h2>pause Method</h2>
     * Pauses the Clip if it is playing.
     */

    public void pause() {
        if (playableClip.isActive()) {
            playableClip.stop();
        }
    }

    /**
     * <h2>stop Method</h2>
     * Stops the clip and closes it, after resetting it's position, such that it is not paused.
     */

    public void stop() {
        playableClip.setFramePosition(0);
        playableClip.stop();
        playableClip.close();
        ais = null;
    }


    /**
     * <h2>rewind Method</h2>
     * Rewinds the audio clip by a factor of five seconds, by obtaining the microsecond position and then subtract and set it.
     */

    public void rewind() {
        if (!(playableClip.isActive())) {
            long newPosition = playableClip.getMicrosecondPosition() - 5000000;
            if (newPosition <= 0) {
                newPosition = 0;
            }
            playableClip.setMicrosecondPosition(newPosition);
        }
    }


    /**
     * <h2>forward Method</h2>
     * Forwards the audio clip by a factor of five seconds, by obtaining the microsecond position and then add and set it.
     */

    public void forward() {
        if (!(playableClip.isActive())) {
            long newPosition = playableClip.getMicrosecondPosition() + 5000000;
            if (newPosition >= playableClip.getMicrosecondLength()) {
                newPosition = 0;
            }
            playableClip.setMicrosecondPosition(newPosition);
        }
    }


    /**
     * <h2>relocate Method</h2>
     * Changes the playback position of the audio clip, depending on the parameter in microseconds.
     *
     * @param newPosition - the relocation point in seconds
     */

    public void relocate(long newPosition) {
        if (!(playableClip.isActive())) {
            playableClip.setMicrosecondPosition(newPosition * 1000000);
        }
    }

    /**
     * <h2>muteOn Method</h2>
     * Sets the value of muteController and mute variables to true, if the opposite is stored in them.
     */

    public void muteOn() {
        if (!muteController.getValue()) {
            muteController.setValue(true);
            mute = true;
        }
    }


    /**
     * <h2>muteOff Method</h2>
     * Sets the value of muteController and mute variables to false, if the opposite is stored in them.
     */

    public void muteOff() {
        if (muteController.getValue()) {
            muteController.setValue(false);
            mute = false;
        }
    }


    /**
     * <h2>volumeUp Method</h2>
     * Increases the volume of the clip through volController by a factor of 1.<br>
     * Also stores the factor value in the volumeLevel variable.
     */

    public void volumeUp() {
        if (volumeLevel < volController.getMaximum()) {
            volumeLevel += 1.0f;
            volController.setValue(volumeLevel);
        }
    }

    /**
     * <h2>volumeUp Method</h2>
     * Decreases the volume of the clip through volController by a factor of 1.<br>
     * Also stores the factor value in the volumeLevel variable.
     */

    public void volumeDown() {
        if (volumeLevel > volController.getMinimum()) {
            volumeLevel -= 1.0f;
            volController.setValue(volumeLevel);
        }
    }

    /**
     * <h2>fetchEndTime Method</h2>
     * Obtains the length of the clip in microseconds and converts it into a string that is in the format of hh:mm:ss.<br>
     * Helps in updating the endTime label in the UI.
     *
     * @return A string containing the total time of the clip in hh:mm:ss format.
     */

    public String fetchEndTime() {
        String retVal = "";
        double secTime = (playableClip.getMicrosecondLength()) / 1000000;

        int hours = (int) secTime / (60 * 60);
        secTime -= hours * 60 * 60;
        int minutes = (int) secTime / 60;
        secTime -= minutes * 60;
        int seconds = (int) secTime;

        if (hours / 10 == 0) {
            retVal = retVal + "0" + hours + ":";
        } else {
            retVal = retVal + hours + ":";
        }

        if (minutes / 10 == 0) {
            retVal = retVal + "0" + minutes + ":";
        } else {
            retVal = retVal + minutes + ":";
        }


        if (seconds / 10 == 0) {
            retVal = retVal + "0" + seconds;
        } else {
            retVal = retVal + seconds;
        }

        return retVal;
    }


    /**
     * <h2>fetchElapsedTime Method</h2>
     * Obtains the elapsed time of the clip in microseconds and converts it into a string that is in the format of hh:mm:ss.<br>
     * Helps in updating the begTime label in the UI.
     *
     * @return A string containing the time elapsed of the clip in hh:mm:ss format.
     */

    public String fetchElapsedTime() {
        String retVal = "";
        double secTime = (playableClip.getMicrosecondPosition()) / 1000000;

        int hours = (int) secTime / (60 * 60);
        secTime -= hours * 60 * 60;
        int minutes = (int) secTime / 60;
        secTime -= minutes * 60;
        int seconds = (int) secTime;

        if (hours / 10 == 0) {
            retVal = retVal + "0" + hours + ":";
        } else {
            retVal = retVal + hours + ":";
        }

        if (minutes / 10 == 0) {
            retVal = retVal + "0" + minutes + ":";
        } else {
            retVal = retVal + minutes + ":";
        }


        if (seconds / 10 == 0) {
            retVal = retVal + "0" + seconds;
        } else {
            retVal = retVal + seconds;
        }

        return retVal;
    }


    /**
     * <h2>fetchEndTimeSec Method</h2>
     * Returns the total time of the clip in seconds. Helps with the slider in the UI.
     *
     * @return An integer value of the total time in seconds.
     */

    public int fetchEndTimeSec() {
        int secTime = (int) (playableClip.getMicrosecondLength()) / 1000000;
        return secTime;
    }


    /**
     * <h2>fetchElapsedTimeSec Method</h2>
     * Returns the time elapsed of the clip in seconds. Helps with the slider in the UI.
     *
     * @return An integer value of the time elapsed in seconds.
     */

    public int fetchElapsedTimeSec() {
        int secTime = (int) (playableClip.getMicrosecondPosition()) / 1000000;
        return secTime;
    }


    /**
     * <h2>getClipPosition Method</h2>
     * Returns the current position of the clip in frame number
     */

    public int getClipPosition() {
        return playableClip.getFramePosition();
    }


    /**
     * <h2>getClipFrames Method</h2>
     * Returns the total duration of the clip in frame number
     */

    public int getClipFrames() {
        return playableClip.getFrameLength();
    }

}
