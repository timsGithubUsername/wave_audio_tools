package ui;

import data.FourierData;
import data.MusicTrack;
import data.WaveMusicTrackImpl1;
import math.ConvolveProcessor;
import math.FourierProcessor;
import tools.MusicPlayer;
import tools.Ressources;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainWindow {
    private MusicPlayer musicPlayer = MusicPlayer.getInstance();
    private AtomicBoolean calculateHeavy = new AtomicBoolean(false);
    private BufferedImage image;
    private Ressources rec = Ressources.getInstance();

    private JButton loadButton;
    private JButton playPauseButton;
    private JCheckBox loopCheckBox;
    private JButton convolveButton;
    private JComboBox trackComboBox;
    private DefaultComboBoxModel trackComboBoxModel;
    private JComboBox convolveComboBox;
    private DefaultComboBoxModel convolveComboBoxModel;
    private JButton saveConvolvedButton;
    private JButton waveButton;
    private JButton specButton;
    private JButton saveGraphicButton;
    private JProgressBar progressBar1;
    private JPanel mainPanel;
    private JLabel describtion;
    private JPanel graphicPanel;

    public MainWindow() {
        initLoadButton();

        initTrackComboBox();
        initConvolveComboBox();

        initButtons();
        setButtonsActive();
    }
    private void initButtons() {
        initWaveButton();
        initSpecButton();
        initConvolveButton();
        initPlayButton();
        initSaveConvolveButton();
        initSaveGraphicButton();
    }
    private void setButtonsActive(){
        waveButtonSetActive();
        specButtonSetActive();
        saveConvolvedButtonSetActive();
        convolveButtonSetActive();
        playButtonSetActive();
        saveGraphicButtonSetActive();
    }
    //= = = = = SPEC_BUTTON = = = = =
    private void initSpecButton(){
        specButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateHeavy.compareAndSet(false, true);
                specButton.setEnabled(false);
                convolveButton.setEnabled(false);
                FourierProcessor processor = new FourierProcessor();

                Thread progressBarAsync = new Thread(() -> {
                    while (calculateHeavy.get()){
                        progressBar1.setValue(processor.getProgress());
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                });
                Thread calculateFourierAsync = new Thread(() -> {
                    FourierData fd = ((ComboItem) trackComboBoxModel.getSelectedItem()).getValue().getFourierData(processor);
                    graphicPanel.removeAll();
                    BufferedImage tempImage = scaledImage(graphicPanel.getWidth(), graphicPanel.getHeight(), fd.getPlotMagnitude());
                    graphicPanel.add(new JLabel(new ImageIcon(tempImage)));
                    graphicPanel.getParent().revalidate();
                    calculateHeavy.compareAndSet(true, false);
                    progressBar1.setValue(0);
                    image = fd.getPlotMagnitude();

                    setButtonsActive();
                });

                calculateFourierAsync.start();
                progressBarAsync.start();
            }
        });
    }
    private boolean specButtonSetActive() {
        if (((ComboItem) trackComboBoxModel.getSelectedItem()) == null || calculateHeavy.get()) {
            specButton.setEnabled(false);
            return true;
        }
        specButton.setEnabled(true);
        return false;
    }

    //= = = = = WAVE_BUTTON = = = = =
    private void initWaveButton() {
        waveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                graphicPanel.removeAll();
                BufferedImage tempImage = scaledImage(graphicPanel.getWidth(), graphicPanel.getHeight(), ((ComboItem) trackComboBoxModel.getSelectedItem()).getValue().getWavePlot());
                graphicPanel.add(new JLabel(new ImageIcon(tempImage)));
                graphicPanel.getParent().revalidate();
                image = ((ComboItem) trackComboBoxModel.getSelectedItem()).getValue().getWavePlot();

                setButtonsActive();
            }
        });
    }
    private boolean waveButtonSetActive() {
        if (((ComboItem) trackComboBoxModel.getSelectedItem()) == null) {
            waveButton.setEnabled(false);
            return true;
        }
        waveButton.setEnabled(true);
        return false;
    }

    //= = = = = SAVE_GRAPHIC_BUTTON = = = = =
    private void initSaveGraphicButton() {
        saveGraphicButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();

                fileChooser.setDialogTitle("Save Graphic");
                fileChooser.setSelectedFile(new File("unkreativ_graphic.png"));
                int status = fileChooser.showSaveDialog(saveConvolvedButton);

                if(status == JFileChooser.APPROVE_OPTION){
                    File save = fileChooser.getSelectedFile();
                    if(!save.exists()){
                        try {
                            save.createNewFile();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }

                    try {
                        ImageIO.write(image, "png", save);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }
    private void saveGraphicButtonSetActive() {
        if (image == null) {
            saveGraphicButton.setEnabled(false);
            return;
        }
        saveGraphicButton.setEnabled(true);
    }

    //= = = = = SAVE_CONVOLVE_BUTTON = = = = =
    private void initSaveConvolveButton() {
        saveConvolvedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                MusicTrack track = ((ComboItem) trackComboBoxModel.getSelectedItem()).getValue();

                fileChooser.setDialogTitle("Save Track "+track.getName());
                fileChooser.setSelectedFile(new File(track.getName()));
                int status = fileChooser.showSaveDialog(saveConvolvedButton);

                if(status == JFileChooser.APPROVE_OPTION){
                    File save = fileChooser.getSelectedFile();
                    if(!save.exists()){
                        try {
                            save.createNewFile();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }

                    FileOutputStream os = null;
                    try {
                        os = new FileOutputStream(save);
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    }
                    InputStream is = new ByteArrayInputStream(track.getMusicDataByteArray());
                    AudioFormat fmt = track.getFormat();
                    AudioInputStream ais = new AudioInputStream(is, fmt, track.getMusicDataByteArray().length);
                    try {
                        AudioSystem.write(ais, AudioFileFormat.Type.WAVE, os);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }
    private void saveConvolvedButtonSetActive() {
        if (((ComboItem) trackComboBoxModel.getSelectedItem()) == null) {
            saveConvolvedButton.setEnabled(false);
            return;
        }
        saveConvolvedButton.setEnabled(true);
    }

    //= = = = = CONVOLVE_BUTTON = = = = =
    private void initConvolveButton() {
        convolveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateHeavy.compareAndSet(false, true);
                specButton.setEnabled(false);
                convolveButton.setEnabled(false);
                ConvolveProcessor processor = new ConvolveProcessor();

                Thread progressBarAsync = new Thread(() -> {
                    while (calculateHeavy.get()){
                        progressBar1.setValue(processor.getProgress());
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                });
                Thread convolveAsync = new Thread(() -> {
                    MusicTrack trackOrigin = ((ComboItem) trackComboBoxModel.getSelectedItem()).getValue();
                    MusicTrack track = new WaveMusicTrackImpl1(
                            processor.convolve(trackOrigin.getSampleArray(),
                                    ((ComboItem) convolveComboBoxModel.getSelectedItem()).getValue().getSampleArray()),
                            trackOrigin.getFormat(),
                            trackOrigin.getFormatByteArray(),
                            "Convolved_" +trackOrigin.getName());
                    addTrackToTrackComboBox(track);
                    specButton.setEnabled(true);
                    convolveButton.setEnabled(true);
                    calculateHeavy.compareAndSet(true, false);
                    progressBar1.setValue(0);
                });
                convolveAsync.start();
                progressBarAsync.start();
            }
        });
    }
    private boolean convolveButtonSetActive() {
        if (((ComboItem) convolveComboBoxModel.getSelectedItem()) == null ||
                ((ComboItem) trackComboBoxModel.getSelectedItem()) == null ||
                calculateHeavy.get()) {
            convolveButton.setEnabled(false);
            return true;
        }
        convolveButton.setEnabled(true);
        return false;
    }

    //= = = = = CONVOLVE_COMBO_BOX = = = = =
    private void initConvolveComboBox() {
        convolveComboBoxModel = new DefaultComboBoxModel();
        convolveComboBox.setModel(convolveComboBoxModel);

        for(int i = 0; i < rec.getImpulseResponses().size(); i++) addTrackToConvolveComboBox(rec.getImpulseResponses().get(i));
    }
    private void addTrackToConvolveComboBox(MusicTrack track) {
        convolveComboBoxModel.addElement(new ComboItem(track.getName(), track));
        setButtonsActive();
    }
    //= = = = = LOAD_BUTTON = = = = =
    private void initLoadButton() {
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int status = fileChooser.showOpenDialog(loadButton);
                fileChooser.setMultiSelectionEnabled(false);

                if (status == JFileChooser.APPROVE_OPTION) {
                    addTrackToTrackComboBox(new WaveMusicTrackImpl1(fileChooser.getSelectedFile()));
                }
            }
        });
    }

    //= = = = = TRACK_COMBO_BOX = = = = =
    private void initTrackComboBox() {
        trackComboBoxModel = new DefaultComboBoxModel();
        trackComboBox.setModel(trackComboBoxModel);
    }

    private void addTrackToTrackComboBox(MusicTrack track) {
        trackComboBoxModel.addElement(new ComboItem(track.getName(), track));
        setButtonsActive();
    }

    //= = = = = Play_Button = = = = =
    private void initPlayButton() {
        playPauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (playPauseButton.getText().equals("Play")) {
                    playPauseButton.setText("Stop");
                    musicPlayer.play(((ComboItem) trackComboBoxModel.getSelectedItem()).getValue(), loopCheckBox.isSelected());
                    Thread buttonWatcher = new Thread(() -> {
                       boolean trackPlays = true;
                       while(trackPlays){
                           try {
                               Thread.sleep(1000);
                           } catch (InterruptedException ex) {
                               ex.printStackTrace();
                           }
                           trackPlays = musicPlayer.isTrackPlaying();
                       }
                       playPauseButton.setText("Play");
                    });
                    buttonWatcher.start();
                } else {
                    playPauseButton.setText("Play");
                    musicPlayer.stop();
                }
            }
        });
    }
    private boolean playButtonSetActive() {
        if (((ComboItem) trackComboBoxModel.getSelectedItem()) == null) {
            playPauseButton.setEnabled(false);
            return true;
        }
        playPauseButton.setEnabled(true);
        return false;
    }

    //= = = = = UTIL = = = = =
    public JPanel getMainPanel() {
        return mainPanel;
    }

    private BufferedImage scaledImage(int width, int height, BufferedImage source) {
        double xFactor = source.getWidth() / (double) width;
        double yFactor = source.getHeight() / (double) height;

        if (xFactor > yFactor) {
            int newW = (int) (source.getWidth() / xFactor);
            int newH = (int) (source.getHeight() / xFactor);
            Image img = source.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
            BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

            Graphics2D g2d = dimg.createGraphics();
            g2d.drawImage(img, 0, 0, null);
            g2d.dispose();

            return dimg;
        } else {
            int newW = (int) (source.getWidth() / yFactor);
            int newH = (int) (source.getHeight() / yFactor);
            Image img = source.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
            BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

            Graphics2D g2d = dimg.createGraphics();
            g2d.drawImage(img, 0, 0, null);
            g2d.dispose();

            return dimg;
        }
    }
}
