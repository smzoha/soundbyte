package app;

import driver.SBRunner;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;


public class Main {

    private JFrame frmSoundbyte;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Main window = new Main();
                    window.frmSoundbyte.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public Main() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        final SBRunner sbmain = new SBRunner();
        final String timeStampDefault = "--:--:--";


        frmSoundbyte = new JFrame();
        frmSoundbyte.setFont(new Font("Arial", Font.PLAIN, 12));
        frmSoundbyte.getContentPane().setBackground(Color.BLACK);
        frmSoundbyte.setTitle("SoundByte");
        frmSoundbyte.setResizable(false);
        frmSoundbyte.setBounds(100, 100, 762, 414);
        frmSoundbyte.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmSoundbyte.getContentPane().setLayout(null);

        JLabel lblAbout = new JLabel("<html>Version 3.0 (STABLE)<br>Build Date: 01/02/15<br>A ZedApps Application developed by Shamah M Zoha</html>");
        lblAbout.setFont(new Font("Tahoma", Font.PLAIN, 9));
        lblAbout.setHorizontalAlignment(SwingConstants.LEFT);
        lblAbout.setForeground(Color.GRAY);
        lblAbout.setBounds(10, 5, 326, 41);
        frmSoundbyte.getContentPane().add(lblAbout);


        final JLabel lblStatusBar = new JLabel("SoundByte Ready");
        lblStatusBar.setHorizontalAlignment(SwingConstants.RIGHT);
        lblStatusBar.setVerticalAlignment(SwingConstants.TOP);
        lblStatusBar.setForeground(Color.DARK_GRAY);
        lblStatusBar.setBounds(10, 360, 736, 23);
        frmSoundbyte.getContentPane().add(lblStatusBar);


        final JLabel lblBegtime = new JLabel(timeStampDefault);
        lblBegtime.setHorizontalAlignment(SwingConstants.CENTER);
        lblBegtime.setForeground(Color.DARK_GRAY);
        lblBegtime.setBounds(10, 59, 55, 14);
        frmSoundbyte.getContentPane().add(lblBegtime);


        final JSlider slider = new JSlider();
        slider.setPaintTicks(true);
        slider.setValue(0);
        slider.setMinorTickSpacing(10);
        slider.setMajorTickSpacing(10);
        slider.setOpaque(false);
        slider.setForeground(Color.DARK_GRAY);
        slider.setBounds(65, 59, 613, 23);
        frmSoundbyte.getContentPane().add(slider);


        final JLabel lblEndtime = new JLabel(timeStampDefault);
        lblEndtime.setHorizontalAlignment(SwingConstants.CENTER);
        lblEndtime.setForeground(Color.DARK_GRAY);
        lblEndtime.setBounds(676, 59, 70, 14);
        frmSoundbyte.getContentPane().add(lblEndtime);


        class timeUpdateThread extends Thread {
            public boolean runFlag = true;

            public void run() {
                while (runFlag) {
                    if (!(lblEndtime.getText().equals(sbmain.endTime()))) {
                        lblEndtime.setText(sbmain.endTime());
                        slider.setMaximum(sbmain.endTimeSec());
                    }

                    lblBegtime.setText(sbmain.elapsedTime());
                    slider.setValue(sbmain.elapsedTimeSec());
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        }

        class pbListenerThread extends Thread {
            public boolean holdFlag = false;

            public void run() {
                while (true) {
                    if ((!holdFlag) && (sbmain.getClipPosiiton() == (sbmain.getClipFrames() - 10))) {
                        sbmain.stop();
                        sbmain.playNext();
                    }
                }
            }
        }


        final pbListenerThread pbListener = new pbListenerThread();
        final timeUpdateThread tUpdate = new timeUpdateThread();

        slider.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (sbmain.isPLEmpty()) {
                    lblStatusBar.setText("Please open a file first");
                } else {
                    tUpdate.suspend();
                }
            }

            public void mouseReleased(MouseEvent e) {
                if (sbmain.isPLEmpty()) {
                    lblStatusBar.setText("Please open a file first");
                } else {
                    long newPos = ((JSlider) e.getSource()).getValue();
                    sbmain.relocate(newPos);
                    lblStatusBar.setText("Playback moved to dragged position");
                    tUpdate.resume();
                    sbmain.play();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(10, 268, 736, 85);
        frmSoundbyte.getContentPane().add(scrollPane);

        final JList<String> list = new JList<String>();
        scrollPane.setViewportView(list);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        list.setForeground(new Color(0, 0, 128));
        list.setBackground(UIManager.getColor("TextField.highlight"));
        final DefaultListModel<String> listModel = new DefaultListModel<String>();
        list.setModel(listModel);
        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent arg0) {
                if (arg0.getClickCount() == 2) {

                    if (sbmain.pbStatus()) {
                        sbmain.stop();
                        pbListener.holdFlag = true;

                        sbmain.playSpec(list.getSelectedIndex());

                        if (!tUpdate.isAlive()) {
                            tUpdate.start();
                        } else {
                            tUpdate.resume();
                        }

                        pbListener.holdFlag = false;
                    } else {
                        sbmain.playSpec(list.getSelectedIndex());

                        if (!tUpdate.isAlive()) {
                            tUpdate.start();
                        } else {
                            tUpdate.resume();
                        }

                        pbListener.start();
                    }

                    lblStatusBar.setText("Playing...");
                }
            }
        });


        final JButton btnStop = new JButton("Stop");
        btnStop.setForeground(Color.WHITE);
        btnStop.setBackground(Color.DARK_GRAY);
        btnStop.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent arg0) {
                if (sbmain.isPLEmpty()) {
                    lblStatusBar.setText("Please open a file first");
                } else {

                    if (tUpdate.isAlive()) {
                        tUpdate.suspend();
                    }

                    pbListener.holdFlag = true;
                    sbmain.stop();
                    lblBegtime.setText(timeStampDefault);
                    lblEndtime.setText(timeStampDefault);
                    slider.setValue(0);
                    lblStatusBar.setText("Playback Stopped");
                }
            }
        });
        btnStop.setBounds(192, 103, 89, 23);
        frmSoundbyte.getContentPane().add(btnStop);


        final JButton btnPlay = new JButton("Play");
        btnPlay.setForeground(Color.WHITE);
        btnPlay.setBackground(Color.DARK_GRAY);
        btnPlay.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent arg0) {
                if (sbmain.isPLEmpty()) {
                    lblStatusBar.setText("Please open a file first");
                } else {
                    sbmain.play();

                    if (pbListener.isAlive()) {
                        pbListener.holdFlag = false;
                    } else {
                        pbListener.start();
                    }

                    if (!tUpdate.isAlive()) {
                        tUpdate.start();
                    } else {
                        tUpdate.resume();
                    }

                    lblStatusBar.setText("Playing...");
                }
            }
        });
        btnPlay.setBounds(10, 103, 89, 23);
        frmSoundbyte.getContentPane().add(btnPlay);


        final JButton btnPause = new JButton("Pause");
        btnPause.setForeground(Color.WHITE);
        btnPause.setBackground(Color.DARK_GRAY);
        btnPause.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (sbmain.isPLEmpty()) {
                    lblStatusBar.setText("Please open a file first");
                } else {
                    pbListener.holdFlag = true;
                    sbmain.pause();
                    lblStatusBar.setText("Playback Paused");
                }
            }
        });
        btnPause.setBounds(101, 103, 89, 23);
        frmSoundbyte.getContentPane().add(btnPause);


        final JButton btnPrevious = new JButton("Previous");
        btnPrevious.setForeground(Color.WHITE);
        btnPrevious.setBackground(Color.DARK_GRAY);
        btnPrevious.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent prevAction) {
                if (sbmain.isPLEmpty()) {
                    lblStatusBar.setText("Please open a file first");
                } else {
                    pbListener.holdFlag = true;
                    sbmain.stop();
                    sbmain.playPrev();

                    if (!tUpdate.isAlive()) {
                        tUpdate.start();
                    } else {
                        tUpdate.resume();
                    }

                    pbListener.holdFlag = false;

                    lblStatusBar.setText("Playing previous file");
                }
            }
        });
        btnPrevious.setBounds(10, 155, 89, 23);
        frmSoundbyte.getContentPane().add(btnPrevious);


        final JButton btnOpenFile = new JButton("Open File");
        btnOpenFile.setForeground(Color.WHITE);
        btnOpenFile.setBackground(Color.DARK_GRAY);
        btnOpenFile.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent saveAction) {
                sbmain.clear();
                listModel.clear();

                if (sbmain.pbStatus()) {

                    if (tUpdate.isAlive()) {
                        tUpdate.suspend();
                    }

                    sbmain.stop();
                    slider.setValue(0);
                    lblBegtime.setText(timeStampDefault);
                    lblEndtime.setText(timeStampDefault);
                    lblStatusBar.setText("Playback Stopped");
                }

                JFileChooser JFC = new JFileChooser();
                FileNameExtensionFilter f1 = new FileNameExtensionFilter("Generic Audio File Types (.wav, .aiff, .au, .ogg)", "wav", "aiff", "au", "ogg");
                FileNameExtensionFilter f2 = new FileNameExtensionFilter("MP3 Audio Files (.mp3)", "mp3");
                JFC.setFileFilter(f1);
                JFC.setFileFilter(f2);
                JFC.setMultiSelectionEnabled(true);
                int flag = JFC.showOpenDialog(frmSoundbyte);
                if (flag == JFileChooser.APPROVE_OPTION) {
                    sbmain.addToPL(JFC.getSelectedFiles());
                    String[] fileNames = sbmain.fetchPL();
                    for (int i = 0; i < fileNames.length; i++) {
                        listModel.addElement(fileNames[i]);
                    }

                    lblStatusBar.setText("File Added to Playlist. Press Play or double click on file name to proceed.");
                }
            }
        });
        btnOpenFile.setBounds(192, 155, 89, 23);
        frmSoundbyte.getContentPane().add(btnOpenFile);


        final JButton btnNext = new JButton("Next");
        btnNext.setForeground(Color.WHITE);
        btnNext.setBackground(Color.DARK_GRAY);
        btnNext.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent nextAction) {
                if (sbmain.isPLEmpty()) {
                    lblStatusBar.setText("Please open a file first");
                } else {
                    pbListener.holdFlag = true;
                    sbmain.stop();
                    sbmain.playNext();

                    if (!tUpdate.isAlive()) {
                        tUpdate.start();
                    } else {
                        tUpdate.resume();
                    }

                    pbListener.holdFlag = false;

                    lblStatusBar.setText("Playing next file");
                }
            }
        });
        btnNext.setBounds(101, 155, 89, 23);
        frmSoundbyte.getContentPane().add(btnNext);


        final JButton btnReset = new JButton("Reset");
        btnReset.setForeground(Color.WHITE);
        btnReset.setBackground(Color.DARK_GRAY);
        btnReset.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (sbmain.isPLEmpty()) {
                    lblStatusBar.setText("Please open a file first");
                } else {
                    sbmain.stop();
                    sbmain.playFirst();

                    if (!tUpdate.isAlive()) {
                        tUpdate.start();
                    } else {
                        tUpdate.resume();
                    }

                    lblStatusBar.setText("Playing from start");
                }
            }
        });
        btnReset.setBounds(101, 129, 89, 23);
        frmSoundbyte.getContentPane().add(btnReset);


        final JButton btnRewind = new JButton("Rewind");
        btnRewind.setForeground(Color.WHITE);
        btnRewind.setBackground(Color.DARK_GRAY);
        btnRewind.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent rewindAction) {
                if (sbmain.isPLEmpty()) {
                    lblStatusBar.setText("Please open a file first");
                } else {
                    sbmain.rewind();
                    lblStatusBar.setText("Playback position set behind by 5 seconds");
                    sbmain.play();
                }
            }
        });
        btnRewind.setBounds(10, 129, 89, 23);
        frmSoundbyte.getContentPane().add(btnRewind);


        final JButton btnForward = new JButton("Forward");
        btnForward.setForeground(Color.WHITE);
        btnForward.setBackground(Color.DARK_GRAY);
        btnForward.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent forwardAction) {
                if (sbmain.isPLEmpty()) {
                    lblStatusBar.setText("Please open a file first");
                } else {
                    sbmain.forward();
                    lblStatusBar.setText("Playback position set forward by 5 seconds");
                    sbmain.play();
                }
            }
        });
        btnForward.setBounds(192, 129, 89, 23);
        frmSoundbyte.getContentPane().add(btnForward);


        final JToggleButton tglbtnMute = new JToggleButton("Mute");
        tglbtnMute.setForeground(Color.WHITE);
        tglbtnMute.setBackground(Color.DARK_GRAY);
        tglbtnMute.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent ie) {
                if (ie.getStateChange() == ItemEvent.SELECTED) {
                    sbmain.muteOn();
                    lblStatusBar.setText("Mute on");
                } else {
                    sbmain.muteOff();
                    lblStatusBar.setText("Mute off");
                }
            }
        });
        tglbtnMute.setBounds(376, 129, 89, 23);
        frmSoundbyte.getContentPane().add(tglbtnMute);


        final JButton btnVolUp = new JButton("Vol Up");
        btnVolUp.setForeground(Color.WHITE);
        btnVolUp.setBackground(Color.DARK_GRAY);
        btnVolUp.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent arg0) {
                sbmain.volUp();
            }
        });
        btnVolUp.setBounds(376, 103, 89, 23);
        frmSoundbyte.getContentPane().add(btnVolUp);


        final JButton btnVolDwn = new JButton("Vol Down");
        btnVolDwn.setForeground(Color.WHITE);
        btnVolDwn.setBackground(Color.DARK_GRAY);
        btnVolDwn.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                sbmain.volDown();
            }
        });
        btnVolDwn.setBounds(376, 155, 89, 23);
        frmSoundbyte.getContentPane().add(btnVolDwn);


        final JToggleButton tglbtnShuffle = new JToggleButton("Shuffle");
        tglbtnShuffle.setForeground(Color.WHITE);
        tglbtnShuffle.setBackground(Color.DARK_GRAY);
        final JToggleButton tglbtnRepeatOne = new JToggleButton("Repeat One");
        tglbtnRepeatOne.setForeground(Color.WHITE);
        tglbtnRepeatOne.setBackground(Color.DARK_GRAY);
        final JToggleButton tglbtnRepeatAll = new JToggleButton("Repeat All");
        tglbtnRepeatAll.setForeground(Color.WHITE);
        tglbtnRepeatAll.setBackground(Color.DARK_GRAY);


        tglbtnShuffle.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent ie) {
                if (ie.getStateChange() == ItemEvent.SELECTED) {
                    sbmain.shuffleOn();

                    if (tglbtnRepeatOne.isSelected()) {
                        tglbtnRepeatOne.setSelected(false);
                        sbmain.repeatOneOff();
                    }

                    if (tglbtnRepeatAll.isSelected()) {
                        tglbtnRepeatAll.setSelected(false);
                        sbmain.repeatAllOff();
                    }

                    lblStatusBar.setText("Shuffle turned on");
                } else {
                    sbmain.shuffleOff();
                    lblStatusBar.setText("Shuffle turned off");
                }
            }
        });
        tglbtnShuffle.setBounds(431, 197, 89, 23);
        frmSoundbyte.getContentPane().add(tglbtnShuffle);


        tglbtnRepeatOne.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent ie) {
                if (ie.getStateChange() == ItemEvent.SELECTED) {
                    sbmain.repeatOneOn();

                    if (tglbtnShuffle.isSelected()) {
                        tglbtnShuffle.setSelected(false);
                        sbmain.shuffleOff();
                    }

                    if (tglbtnRepeatAll.isSelected()) {
                        tglbtnRepeatAll.setSelected(false);
                        sbmain.repeatAllOff();
                    }

                    lblStatusBar.setText("Repeat One turned on");
                } else {
                    sbmain.repeatOneOff();
                    lblStatusBar.setText("Repeat One turned off");
                }
            }
        });
        tglbtnRepeatOne.setBounds(524, 197, 110, 23);
        frmSoundbyte.getContentPane().add(tglbtnRepeatOne);


        tglbtnRepeatAll.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent ie) {
                if (ie.getStateChange() == ItemEvent.SELECTED) {
                    sbmain.repeatAllOn();

                    if (tglbtnRepeatOne.isSelected()) {
                        tglbtnRepeatOne.setSelected(false);
                        sbmain.repeatOneOff();
                    }

                    if (tglbtnShuffle.isSelected()) {
                        tglbtnShuffle.setSelected(false);
                        sbmain.shuffleOff();
                    }

                    lblStatusBar.setText("Repeat All turned on");
                } else {
                    sbmain.repeatAllOff();
                    lblStatusBar.setText("Repeat All turned off");
                }
            }
        });
        tglbtnRepeatAll.setBounds(636, 197, 110, 23);
        frmSoundbyte.getContentPane().add(tglbtnRepeatAll);


        final JButton btnClear = new JButton("Clear");
        btnClear.setForeground(Color.WHITE);
        btnClear.setBackground(Color.DARK_GRAY);
        btnClear.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent clrAction) {
                if (sbmain.pbStatus()) {

                    if (tUpdate.isAlive()) {
                        tUpdate.suspend();
                    }

                    sbmain.stop();
                    slider.setValue(0);
                    lblBegtime.setText(timeStampDefault);
                    lblEndtime.setText(timeStampDefault);
                    lblStatusBar.setText("Playback Stopped");
                }

                sbmain.clear();
                listModel.clear();
                lblEndtime.setText(timeStampDefault);
                lblStatusBar.setText("Playlist Cleared");
            }
        });
        btnClear.setBounds(196, 238, 89, 23);
        frmSoundbyte.getContentPane().add(btnClear);


        final JButton btnLoadPL = new JButton("Load");
        btnLoadPL.setForeground(Color.WHITE);
        btnLoadPL.setBackground(Color.DARK_GRAY);
        btnLoadPL.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent arg0) {
                if (sbmain.pbStatus()) {

                    if (tUpdate.isAlive()) {
                        tUpdate.suspend();
                    }

                    sbmain.stop();
                    slider.setValue(0);
                    lblBegtime.setText(timeStampDefault);
                    lblEndtime.setText(timeStampDefault);
                    lblStatusBar.setText("Playback Stopped");
                }

                sbmain.clear();
                listModel.clear();

                JFileChooser JFC = new JFileChooser();
                FileNameExtensionFilter f = new FileNameExtensionFilter("SoundByte Playlist (.sbp)", "sbp");
                JFC.setFileFilter(f);

                int flag = JFC.showOpenDialog(frmSoundbyte);

                if (flag == JFileChooser.APPROVE_OPTION) {
                    File inpFile = JFC.getSelectedFile();
                    sbmain.loadPL(inpFile);

                    String[] fileNames = sbmain.fetchPL();
                    for (int i = 0; i < fileNames.length; i++) {
                        listModel.addElement(fileNames[i]);
                    }
                }
                lblStatusBar.setText("Playlist loaded from file.");
            }
        });
        btnLoadPL.setBounds(10, 238, 89, 23);
        frmSoundbyte.getContentPane().add(btnLoadPL);


        final JButton btnAddPL = new JButton("Add");
        btnAddPL.setForeground(Color.WHITE);
        btnAddPL.setBackground(Color.DARK_GRAY);
        btnAddPL.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent arg0) {
                if (sbmain.pbStatus()) {

                    if (tUpdate.isAlive()) {
                        tUpdate.suspend();
                    }

                    sbmain.stop();
                    slider.setValue(0);
                    lblBegtime.setText(timeStampDefault);
                    lblEndtime.setText(timeStampDefault);
                    lblStatusBar.setText("Playback Stopped.");
                }

                JFileChooser JFC = new JFileChooser();

                FileNameExtensionFilter f1 = new FileNameExtensionFilter("Generic Audio File Types (.wav, .aiff, .au, .ogg)", "wav", "aiff", "au", "ogg");
                FileNameExtensionFilter f2 = new FileNameExtensionFilter("MP3 Audio Files (.mp3)", "mp3");
                JFC.setFileFilter(f1);
                JFC.setFileFilter(f2);
                JFC.setMultiSelectionEnabled(true);

                int flag = JFC.showOpenDialog(frmSoundbyte);

                if (flag == JFileChooser.APPROVE_OPTION) {
                    File[] tmpFileArr = JFC.getSelectedFiles();
                    sbmain.addToPL(tmpFileArr);

                    for (int i = 0; i < tmpFileArr.length; i++) {
                        listModel.addElement(tmpFileArr[i].getName());
                    }

                    lblStatusBar.setText("File Added to Playlist. Press Play or double click on file name to proceed.");
                }
            }
        });
        btnAddPL.setBounds(564, 238, 89, 23);
        frmSoundbyte.getContentPane().add(btnAddPL);


        final JButton btnRemovePL = new JButton("Remove");
        btnRemovePL.setForeground(Color.WHITE);
        btnRemovePL.setBackground(Color.DARK_GRAY);
        btnRemovePL.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent arg0) {
                if (sbmain.pbStatus()) {

                    if (tUpdate.isAlive()) {
                        tUpdate.suspend();
                    }

                    sbmain.stop();
                    slider.setValue(0);
                    lblBegtime.setText(timeStampDefault);
                    lblEndtime.setText(timeStampDefault);
                    lblStatusBar.setText("Playback Stopped.");
                }

                int indxInList = list.getSelectedIndex();
                if (indxInList != -1) {
                    sbmain.removeFromPL(indxInList);
                    listModel.remove(indxInList);
                }
            }
        });
        btnRemovePL.setBounds(657, 238, 89, 23);
        frmSoundbyte.getContentPane().add(btnRemovePL);


        JButton btnSavePL = new JButton("Save");
        btnSavePL.setForeground(Color.WHITE);
        btnSavePL.setBackground(Color.DARK_GRAY);
        btnSavePL.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent openAction) {
                if (sbmain.pbStatus()) {

                    if (tUpdate.isAlive()) {
                        tUpdate.suspend();
                    }

                    sbmain.stop();
                    slider.setValue(0);
                    lblBegtime.setText(timeStampDefault);
                    lblEndtime.setText(timeStampDefault);
                    lblStatusBar.setText("Playback Stopped.");
                }

                JFileChooser JFC = new JFileChooser();
                FileNameExtensionFilter f = new FileNameExtensionFilter("SoundByte Playlist (.sbp)", "sbp");
                JFC.setFileFilter(f);
                int flag = JFC.showOpenDialog(frmSoundbyte);

                if (flag == JFileChooser.APPROVE_OPTION) {
                    File saveFile = new File(JFC.getSelectedFile().getAbsolutePath() + ".sbp");
                    sbmain.savePL(saveFile);
                    lblStatusBar.setText("Playlist saved to file.");
                }
            }
        });
        btnSavePL.setBounds(101, 238, 89, 23);
        frmSoundbyte.getContentPane().add(btnSavePL);


        final JButton btnStartRecord = new JButton("Record");
        btnStartRecord.setForeground(Color.WHITE);
        btnStartRecord.setBackground(Color.DARK_GRAY);
        btnStartRecord.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent arg0) {
                sbmain.clear();
                listModel.clear();
                if (sbmain.pbStatus()) {

                    if (tUpdate.isAlive()) {
                        tUpdate.suspend();
                    }

                    sbmain.stop();
                    slider.setValue(0);
                    lblBegtime.setText(timeStampDefault);
                    lblEndtime.setText(timeStampDefault);
                    lblStatusBar.setText("Playback Stopped.");
                }

                sbmain.startRecord();
                lblStatusBar.setText("Recording Started...");
            }
        });
        btnStartRecord.setBounds(564, 103, 89, 23);
        frmSoundbyte.getContentPane().add(btnStartRecord);


        final JButton btnStopRecord = new JButton("Stop");
        btnStopRecord.setForeground(Color.WHITE);
        btnStopRecord.setBackground(Color.DARK_GRAY);
        btnStopRecord.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                sbmain.stopRecord();
                lblStatusBar.setText("Recording Stopped. Please save file before further recording.");
                String[] fileNames = sbmain.fetchPL();
                for (int i = 0; i < fileNames.length; i++) {
                    listModel.addElement(fileNames[i]);
                }
            }
        });
        btnStopRecord.setBounds(564, 129, 89, 23);
        frmSoundbyte.getContentPane().add(btnStopRecord);


        final JButton btnCancelRecord = new JButton("Discard");
        btnCancelRecord.setForeground(Color.WHITE);
        btnCancelRecord.setBackground(Color.DARK_GRAY);
        btnCancelRecord.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent arg0) {
                if (sbmain.pbStatus()) {

                    if (tUpdate.isAlive()) {
                        tUpdate.suspend();
                    }

                    sbmain.stop();
                    slider.setValue(0);
                    lblBegtime.setText(timeStampDefault);
                    lblEndtime.setText(timeStampDefault);
                    lblStatusBar.setText("Playback Stopped.");
                }

                sbmain.cancelRecord();
                listModel.clear();
                sbmain.clear();
                lblStatusBar.setText("Recorded File Discarded...");
            }
        });
        btnCancelRecord.setBounds(657, 129, 89, 23);
        frmSoundbyte.getContentPane().add(btnCancelRecord);


        final JButton btnSaveFile = new JButton("Save");
        btnSaveFile.setForeground(Color.WHITE);
        btnSaveFile.setBackground(Color.DARK_GRAY);
        btnSaveFile.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent arg0) {
                if (sbmain.pbStatus()) {

                    if (tUpdate.isAlive()) {
                        tUpdate.suspend();
                    }

                    sbmain.stop();
                    slider.setValue(0);
                    lblBegtime.setText(timeStampDefault);
                    lblEndtime.setText(timeStampDefault);
                    lblStatusBar.setText("Playback Stopped.");
                }


                JFileChooser JFC = new JFileChooser();
                FileNameExtensionFilter f = new FileNameExtensionFilter("Wave Audio File (.wav)", "wav");
                JFC.setFileFilter(f);
                int flag = JFC.showSaveDialog(frmSoundbyte);

                if (flag == JFileChooser.APPROVE_OPTION) {
                    File rawFile = JFC.getSelectedFile();
                    File saveFile = new File(rawFile.getAbsolutePath() + ".wav");

                    sbmain.clear();
                    listModel.clear();
                    sbmain.saveRecord(saveFile);

                    String[] fileNames = sbmain.fetchPL();
                    for (int i = 0; i < fileNames.length; i++) {
                        listModel.addElement(fileNames[i]);
                    }
                }
            }
        });
        btnSaveFile.setBounds(657, 103, 89, 23);
        frmSoundbyte.getContentPane().add(btnSaveFile);

        final JButton btnCutForward = new JButton("Cut Forward");
        btnCutForward.setForeground(Color.WHITE);
        btnCutForward.setBackground(Color.DARK_GRAY);
        btnCutForward.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent arg0) {
                if (sbmain.isPLEmpty()) {
                    lblStatusBar.setText("Please open a file first");
                } else {
                    if (!sbmain.pbStatus()) {
                        File saveFile = null;

                        JFileChooser JFC = new JFileChooser();
                        FileNameExtensionFilter f1 = new FileNameExtensionFilter("Generic Audio File Types (.wav, .aiff, .au, .ogg)", "wav", "aiff", "au", "ogg");
                        JFC.setFileFilter(f1);
                        int flag = JFC.showSaveDialog(frmSoundbyte);

                        if (flag == JFileChooser.APPROVE_OPTION) {
                            saveFile = new File(JFC.getSelectedFile().getAbsolutePath() + ".wav");
                        }

                        if (saveFile != null) {
                            sbmain.cutAudio(saveFile, sbmain.getClipPosiiton(), sbmain.getClipFrames());
                            lblStatusBar.setText("Audio trimmed");

                            if (tUpdate.isAlive()) {
                                tUpdate.suspend();
                            }

                            sbmain.stop();
                            slider.setValue(0);
                            lblBegtime.setText(timeStampDefault);
                            lblEndtime.setText(timeStampDefault);

                            sbmain.clear();
                            listModel.clear();

                            File[] tmpArr = new File[1];
                            tmpArr[0] = saveFile;

                            sbmain.addToPL(tmpArr);

                            String[] fileNames = sbmain.fetchPL();
                            for (int i = 0; i < fileNames.length; i++) {
                                listModel.addElement(fileNames[i]);
                            }

                        } else {
                            lblStatusBar.setText("Some Information Missing. Try again.");
                        }

                    } else {
                        lblStatusBar.setText("Please pause file first");
                    }
                }
            }
        });
        btnCutForward.setBounds(10, 197, 104, 23);
        frmSoundbyte.getContentPane().add(btnCutForward);

        final JButton btnCutBackward = new JButton("Cut Backward");
        btnCutBackward.setForeground(Color.WHITE);
        btnCutBackward.setBackground(Color.DARK_GRAY);
        btnCutBackward.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent arg0) {
                if (sbmain.isPLEmpty()) {
                    lblStatusBar.setText("Please open a file first");
                } else {
                    if (!sbmain.pbStatus()) {
                        File saveFile = null;

                        JFileChooser JFC = new JFileChooser();
                        FileNameExtensionFilter f1 = new FileNameExtensionFilter("Generic Audio File Types (.wav, .aiff, .au, .ogg)", "wav", "aiff", "au", "ogg");
                        JFC.setFileFilter(f1);
                        int flag = JFC.showSaveDialog(frmSoundbyte);

                        if (flag == JFileChooser.APPROVE_OPTION) {
                            saveFile = new File(JFC.getSelectedFile().getAbsolutePath() + ".wav");
                        }

                        if (saveFile != null) {
                            sbmain.cutAudio(saveFile, 0, sbmain.getClipPosiiton());
                            lblStatusBar.setText("Audio trimmed");

                            if (tUpdate.isAlive()) {
                                tUpdate.suspend();
                            }

                            sbmain.stop();
                            slider.setValue(0);
                            lblBegtime.setText(timeStampDefault);
                            lblEndtime.setText(timeStampDefault);

                            sbmain.clear();
                            listModel.clear();

                            File[] tmpArr = new File[1];
                            tmpArr[0] = saveFile;

                            sbmain.addToPL(tmpArr);

                            String[] fileNames = sbmain.fetchPL();
                            for (int i = 0; i < fileNames.length; i++) {
                                listModel.addElement(fileNames[i]);
                            }

                        } else {
                            lblStatusBar.setText("Some Information Missing. Try again.");
                        }

                    } else {
                        lblStatusBar.setText("Please pause file first");
                    }
                }
            }
        });
        btnCutBackward.setBounds(117, 197, 127, 23);
        frmSoundbyte.getContentPane().add(btnCutBackward);


        final JButton btnJoinFiles = new JButton("Join Files");
        btnJoinFiles.setForeground(Color.WHITE);
        btnJoinFiles.setBackground(Color.DARK_GRAY);
        btnJoinFiles.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (sbmain.pbStatus()) {

                    if (tUpdate.isAlive()) {
                        tUpdate.suspend();
                    }

                    sbmain.stop();
                    slider.setValue(0);
                    lblBegtime.setText(timeStampDefault);
                    lblEndtime.setText(timeStampDefault);
                    lblStatusBar.setText("Playback Stopped.");
                }

                JFileChooser JFC = new JFileChooser();
                File joinFile1 = null, joinFile2 = null, newFile = null;

                FileNameExtensionFilter f1 = new FileNameExtensionFilter("Generic Audio File Types (.wav, .aiff, .au, .ogg)", "wav", "aiff", "au", "ogg");
                JFC.setFileFilter(f1);
                JFC.setMultiSelectionEnabled(false);

                int flag = JFC.showOpenDialog(frmSoundbyte);

                if (flag == JFileChooser.APPROVE_OPTION) {
                    joinFile1 = JFC.getSelectedFile();
                }

                JFC.cancelSelection();
                flag = JFC.showOpenDialog(frmSoundbyte);

                if (flag == JFileChooser.APPROVE_OPTION) {
                    joinFile2 = JFC.getSelectedFile();
                }

                JFC.cancelSelection();
                flag = JFC.showSaveDialog(frmSoundbyte);

                if (flag == JFileChooser.APPROVE_OPTION) {
                    newFile = new File(JFC.getSelectedFile().getAbsolutePath() + ".wav");
                }

                if ((joinFile1 != null) && (joinFile2 != null) && (newFile != null)) {
                    sbmain.joinFiles(joinFile1, joinFile2, newFile);

                    sbmain.clear();
                    listModel.clear();

                    File[] tmpArr = new File[1];
                    tmpArr[0] = newFile;

                    sbmain.addToPL(tmpArr);

                    String[] fileNames = sbmain.fetchPL();
                    for (int i = 0; i < fileNames.length; i++) {
                        listModel.addElement(fileNames[i]);
                    }

                    lblStatusBar.setText("Files joined and output has been added to playlist.");

                } else {
                    lblStatusBar.setText("Some Information Missing. Try again.");
                }

            }
        });
        btnJoinFiles.setBounds(247, 197, 89, 23);
        frmSoundbyte.getContentPane().add(btnJoinFiles);


        JSeparator separator = new JSeparator();
        separator.setBackground(Color.RED);
        separator.setForeground(Color.BLACK);
        separator.setBounds(10, 47, 736, 10);
        frmSoundbyte.getContentPane().add(separator);

        JSeparator separator_1 = new JSeparator();
        separator_1.setForeground(Color.BLACK);
        separator_1.setBackground(Color.RED);
        separator_1.setBounds(10, 93, 736, 10);
        frmSoundbyte.getContentPane().add(separator_1);

        JSeparator separator_2 = new JSeparator();
        separator_2.setForeground(Color.BLACK);
        separator_2.setBackground(Color.RED);
        separator_2.setBounds(10, 186, 736, 10);
        frmSoundbyte.getContentPane().add(separator_2);

        JSeparator separator_3 = new JSeparator();
        separator_3.setForeground(Color.BLACK);
        separator_3.setBackground(Color.RED);
        separator_3.setBounds(10, 228, 736, 10);
        frmSoundbyte.getContentPane().add(separator_3);

        JLabel lblLogo = new JLabel("");
        lblLogo.setIcon(new ImageIcon(Main.class.getResource("/sb_main_logo.png")));
        lblLogo.setHorizontalAlignment(SwingConstants.LEFT);
        lblLogo.setBounds(508, 11, 238, 30);
        frmSoundbyte.getContentPane().add(lblLogo);

        JLabel lblBackground = new JLabel("");
        lblBackground.setIcon(new ImageIcon(Main.class.getResource("/sb_bg.png")));
        lblBackground.setBounds(-23, 0, 779, 387);
        frmSoundbyte.getContentPane().add(lblBackground);

    }
}
