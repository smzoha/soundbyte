package modules;

import javax.sound.sampled.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * <h2>RecordModule Module</h2>
 * Enables the user to record audio from a microphone device. Includes an inner class
 * recThread that extends from the Thread class in the Java library to fit the purpose
 * of recording audio in the application.<br>
 * The methods that are included in the class helps the SoundByte application to start
 * and stop recording audio, along with save them in the desired destination of the user
 * or cancel it entirely.<br>
 * The recorded audio is saved in generic wave format, such that it is easy to be played,
 * uploaded and manipulated by the application easily, as well as provide simple accessibility
 * for the user.<br><br>
 *
 * @author Shamah M Zoha
 * @version Release Candidate
 */


public class RecordModule {
    private TargetDataLine line;
    private AudioInputStream ais;
    private AudioFormat recFormat = new AudioFormat(44100, 16, 2, true, true);
    private final AudioFileFormat.Type recFileFormat = AudioFileFormat.Type.WAVE;
    private final File tmpRecFile = new File("tmpRec.wav");

    private recThread rcThread;

    private FileInputStream tmpReader;
    private FileOutputStream saveWriter;


    /**
     * <h2>startRecord Method</h2><br>
     * Enables the SoundByte application to record audio from the user's microphone.<br>
     * It basically instantiates the recThread that was declared earlier, such that the run
     * method that it stores can be used to record in the background, without putting the
     * application on hold for eternity.
     */

    public void startRecord() {
        try {
            line = AudioSystem.getTargetDataLine(recFormat);
            rcThread = new recThread();
            rcThread.start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    /**
     * <h2>stopRecord Method</h2><br>
     * This method helps in stopping the thread that is recording, along with stopping and
     * closing the TargetDataLine for clean up. Finally, the recorded file is returned, such
     * that it can be added to the playlist for playback in the driver class, which links the UI
     * with the modules.
     *
     * @return The File that has been recorded.
     */

    @SuppressWarnings("deprecation")

    public File stopRecord() {
        if (rcThread != null) {
            line.stop();
            line.close();
            rcThread.stop();
        }
        return tmpRecFile;
    }


    /**
     * <h2>cancelRecord Method</h2><br>
     * This can be used once the recording is done. It is used to remove the temporary file
     * that is created in the root folder of the application, to meet the need of the users.
     */

    public void cancelRecord() {
        if (tmpRecFile.exists()) {
            tmpRecFile.delete();
        }
    }


    /**
     * <h2>saveRecord Method</h2>
     * This method is used to copy the temporary file that was created after recording
     * to a directory that is being pointed by the user from the UI.<br>
     * A FileInputStream and FileOutputStream is used to achieve the process described
     * above.<br>
     * Finally, the streams are closed for cleanup.<br><br>
     *
     * @param saveFile The new file that is to be created to store the recorded audio.
     */

    public void saveRecord(File saveFile) {
        try {
            tmpReader = new FileInputStream(tmpRecFile);
            saveWriter = new FileOutputStream(saveFile);

            byte[] buffer = new byte[1024];

            while (tmpReader.read(buffer) != -1) {
                saveWriter.write(buffer);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                tmpReader.close();
                saveWriter.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }


    /**
     * <h2>recThread class</h2>
     * Extends Thread class in the Java libraries.<br><br>
     * This enables the SoundByte application to record audio in background,
     * without even hanging up the application UI.<br>
     * Uses a TargetDataLine that had been created earlier, and with the help
     * of the AudioInputStream, audio is captured from the microphone port of the user's system.<br>
     * The AudioSystem class is then used to write the signals obtained from the AudioInputStream
     * into a temporary file that is stored in the root folder where the application is being stored.<br>
     * <br>
     *
     * @author Shamah M Zoha
     */

    class recThread extends Thread {
        public void run() {
            try {
                line.open(recFormat);
                line.start();
                ais = new AudioInputStream(line);
                AudioSystem.write(ais, recFileFormat, tmpRecFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
