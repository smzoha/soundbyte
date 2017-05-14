package modules;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;


/**
 * <h2>EditModule Module</h2>
 * Enables the user to EditModule audio files.
 *
 * @author Shamah M Zoha
 * @version Release Candidate
 */

public class EditModule {
    private AudioInputStream ais1;
    private AudioInputStream ais2;
    private AudioInputStream outAis;

    /**
     * <h2>cutAudio Method</h2><br>
     * Guideline taken from a solution posted in Stack Overflow.<br>
     * Trims an audio file and saves the output based on the starting and ending positions provided.<br>
     * Helps in achieving both the cut forward and cut backward functions.<br>
     * The method takes in the input and output file, along with the starting and ending position,
     * and creates an audio input stream out of the input file first.<br>
     * A buffer array is then created along with a ByteArrayOutputStream, and the audio file is stored in
     * byte format, in an array then.<br>
     * The starting and end positions are then converted into proper precision next, and with the help of
     * the computed values, the bytes that are required for the process are stored in a different byte array.<br>
     * The audio input stream for the output file is then initialized based on the new byte array, and with the
     * help of AudioSystem class, the output file is written accordingly.
     * <br><br>
     *
     * @param inpFile  The input file
     * @param outFile  The trimmed output file
     * @param startPos The starting position in frames
     * @param endPos   The ending position in frames
     */

    public void cutAudio(File inpFile, File outFile, int startPos, int endPos) {
        try {
            ais1 = AudioSystem.getAudioInputStream(inpFile);
            int bytesPerFrame = ais1.getFormat().getFrameSize();

            byte[] buffer = new byte[bytesPerFrame * 1024];
            ByteArrayOutputStream byteWriter = new ByteArrayOutputStream();

            while (ais1.read(buffer) != -1) {
                byteWriter.write(buffer, 0, buffer.length);
            }

            byte[] audioByte = byteWriter.toByteArray();

            startPos = startPos * bytesPerFrame;
            endPos = endPos * bytesPerFrame;

            byte[] splitAudioByte = new byte[endPos - startPos];


            for (int i = 0; i < (endPos - startPos); i++) {
                splitAudioByte[i] = audioByte[i + startPos];
            }

            ByteArrayInputStream byteReader = new ByteArrayInputStream(splitAudioByte);
            outAis = new AudioInputStream(byteReader, ais1.getFormat(), splitAudioByte.length / ais1.getFormat().getFrameSize());
            AudioSystem.write(outAis, AudioFileFormat.Type.WAVE, outFile);

            ais1.close();
            outAis.close();
            byteWriter.close();
            byteReader.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    /**
     * <h2>joinFiles Method</h2>
     * Takes two wave files and joins them into one.<br>
     * First, the audio input streams are obtained from the two files that are to be joined.<br>
     * Next, using the first file, an encoded input stream is created for the second, such that a mismatch of audio format
     * does not affect the output at all.<br>
     * Using ByteArrayOutputStream, the content of the input streams are converted into byte data and stored into arrays.<br>
     * The resulting arrays are then concatenated, and with the help of a ByteInputStream, the merged data is taken into
     * the output audio input stream.<br>
     * The write method of the AudioSystem class is then used to write out the stream into the newFile object.<br>
     * <br>
     *
     * @param joinFile1 - The file to be joined.
     * @param joinFile2 - Another file that is to be joined.
     * @param newFile   - The file that is to be created to store the output.
     */

    public void joinFiles(File joinFile1, File joinFile2, File newFile) {
        try {
            ais1 = AudioSystem.getAudioInputStream(joinFile1);
            ais2 = AudioSystem.getAudioInputStream(joinFile2);

            AudioInputStream encoded = AudioSystem.getAudioInputStream(ais1.getFormat(), ais2);

            byte[] buffer = new byte[1024 * ais1.getFormat().getFrameSize()];
            ByteArrayOutputStream byteWriter = new ByteArrayOutputStream();

            while (ais1.read(buffer) != -1) {
                byteWriter.write(buffer, 0, buffer.length);
            }

            byte[] fileByte1 = byteWriter.toByteArray();
            byteWriter.reset();

            buffer = new byte[1024 * encoded.getFormat().getFrameSize()];

            while (encoded.read(buffer) != -1) {
                byteWriter.write(buffer, 0, buffer.length);
            }

            byte[] fileByte2 = byteWriter.toByteArray();
            byteWriter.reset();

            int c = 0;

            byte[] outByte = new byte[fileByte1.length + fileByte2.length];

            for (int i = 0; i < fileByte1.length; i++) {
                outByte[c] = fileByte1[i];
                c++;
            }

            for (int i = 0; i < fileByte2.length; i++) {
                outByte[c] = fileByte2[i];
                c++;
            }


            ByteArrayInputStream byteReader = new ByteArrayInputStream(outByte);
            outAis = new AudioInputStream(byteReader, ais1.getFormat(), outByte.length / ais1.getFormat().getFrameSize());
            AudioSystem.write(outAis, AudioFileFormat.Type.WAVE, newFile);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
