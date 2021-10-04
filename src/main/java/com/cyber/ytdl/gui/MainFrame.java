/*
 * The MIT License
 *
 * Copyright 2021 Kirill Bereznyakov.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.cyber.ytdl.gui;

import com.cyber.ui.swing.BagCell;
import com.cyber.ui.swing.BaseFrameWithProperties;
import com.cyber.util.ApplicationProperties;
import com.cyber.ytdl.VideoDownloader;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.util.Arrays;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

/**
 *
 * @author Kirill Bereznyakov
 */
public class MainFrame extends BaseFrameWithProperties{

    // UI Components
    protected JLabel urlLabel;
    protected JLabel selectOutputLabel;

    protected JButton pasteButton;
    protected JTextField urlTextField;
    protected JComboBox<String> qualityComboBox;
    protected JComboBox<String> downloaderComboBox;
    protected JButton downloadButton;
    protected JButton selectOutputPathButton;
    protected JCheckBox compatibilityCheckBox;

    protected JProgressBar progressBar;

    protected JTextArea processOutputText;
    protected JScrollPane processOutputScrollPane;

    // Properties
    protected VideoDownloader downloader;
    protected String outputPath;
    
    // Defaults
    protected static String defaultQuality = "1080";
    protected static String defaultDownloader = "youtube-dl";
    protected static boolean defaultCompatMode = false;

    public MainFrame(ApplicationProperties properties) {
        super("Youtube Downloader GUI", properties);
    }

    @Override
    protected void configure() {
        // Properties
        applyProperties(properties);

        // Components
        initComponents(root);

        // Layout
        initLayout(root);

        // Actions Listeners
        bindActions();

    }

    @Override
    public void initComponents(JPanel root) {

        downloader = new VideoDownloader();

        ImageIcon icon = new ImageIcon(getClass().getResource("/icon.png"));
        setIconImage(icon.getImage());

        qualityComboBox = new JComboBox<>(VideoDownloader.QUALITY_LIST);
        qualityComboBox.setSelectedIndex(Arrays.asList(VideoDownloader.QUALITY_LIST).indexOf(defaultQuality));
        downloaderComboBox = new JComboBox<>(VideoDownloader.DOWNLOADER_LIST);
        downloaderComboBox.setSelectedIndex(Arrays.asList(VideoDownloader.DOWNLOADER_LIST).indexOf(defaultDownloader));

        urlLabel = new JLabel("Enter your youtube link here:");
        urlTextField = new JTextField("");
        selectOutputLabel = new JLabel("Save to: ");

        pasteButton = new JButton("Paste");
        selectOutputPathButton = new JButton("Select output path...");
        if (outputPath!=null && !outputPath.isEmpty()) selectOutputPathButton.setText(outputPath);
        downloadButton = new JButton("Download");
        Font buttonFont = downloadButton.getFont();
        downloadButton.setFont(new Font(buttonFont.getFontName(), Font.BOLD, (int)(buttonFont.getSize()*1.2)));

        compatibilityCheckBox = new JCheckBox("Compatibility mode (avc+aac).mp4");
        compatibilityCheckBox.setSelected(defaultCompatMode);

        processOutputText = new JTextArea();
        processOutputScrollPane = new JScrollPane(processOutputText);
        processOutputScrollPane.setPreferredSize(new Dimension(600,300));
        processOutputScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setVisible(false);
    }

    protected void initLayout(JPanel root){
        root.setLayout(new GridBagLayout());

        // Row 0
        root.add(urlLabel, BagCell.next().alignLeft().endRow() );

        // Row 1
        root.add(pasteButton, BagCell.next() );
        root.add(urlTextField, BagCell.next().fillX(1.0) );
        root.add(qualityComboBox, BagCell.next() );
        root.add(downloaderComboBox, BagCell.next().fillX().endRow() );

        // Row 2
        root.add(selectOutputLabel, BagCell.next() );
        root.add(selectOutputPathButton, BagCell.next().fillX() );  // no row end (empty space for downloadButton)

        // Row 3
        root.add(compatibilityCheckBox, BagCell.row(3).alignLeft().width(2));
        root.add(downloadButton, BagCell.next().fillBoth().height(2).endRow() );

        // Row 4
        root.add(progressBar, BagCell.next().fillX().endRow() );

        // Row 5
        root.add(processOutputScrollPane, BagCell.next().fillBoth().weight(1.0, 1.0).endRow() );

        pack();
    }

    protected void bindActions(){
        downloadButton.addActionListener(e -> {
            if (downloader.isAlive()){
                stopDownloadAction();
            }else{
                startDownloadAction();
            }
        });

        pasteButton.addActionListener(e -> {
            urlTextField.setText("");
            urlTextField.paste();
        });

        selectOutputPathButton.addActionListener(e -> {
            outputPath = selectOutputPath(outputPath);
            if (!outputPath.isEmpty()) selectOutputPathButton.setText(outputPath);
        });

        downloader.onDownloadProgressValue(value -> progressBar.setValue(value.intValue()));
        downloader.onDownloadProgressString(progressBar::setString);

        downloader.onMessage(this::println);
        downloader.onExit(this::stopDownloadAction);
    }

    protected String selectOutputPath(String currentPath){
        JFileChooser dirChooser = new JFileChooser(currentPath);
        dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        dirChooser.setDialogTitle("Select path");
        dirChooser.setAcceptAllFileFilterUsed(false);

        String ret = currentPath;

        if (dirChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            ret = dirChooser.getSelectedFile().toString();
        }

        return ret;
    }

    protected void prepareProgressUI(){
        processOutputText.setText("");
        processOutputScrollPane.scrollRectToVisible(new Rectangle(0,0,0,0));
        progressBar.setValue(0);
        progressBar.setString("");
        progressBar.setVisible(true);
    }

    protected void startDownloadAction(){
        if (urlTextField.getText().isEmpty()) return;
        prepareProgressUI();

        String url = urlTextField.getText();
        String downloaderExe = downloaderComboBox.getSelectedItem().toString();
        String quality = qualityComboBox.getSelectedItem().toString();
        boolean compatibleFormat = compatibilityCheckBox.isSelected();

        println(String.format("%s download: %s", downloaderExe, url));
        println("output path: " + outputPath);

        downloader.download( url, downloaderExe, outputPath, quality, compatibleFormat);
        downloadButton.setText("Stop");
    }

    protected boolean stopDownloadAction(){
        if (downloader.isAlive()){
            int result = JOptionPane.showConfirmDialog( this,
                "Downloading process is not fully completed. Abort anyway?",
                "Interrupt process",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

            if (result!=JOptionPane.YES_OPTION) return false;
            downloader.destroy();
        }
        downloadButton.setText("Download");
        progressBar.setVisible(false);
        return true;
    }


    public void println(String str){
        processOutputText.append(str);
        processOutputText.append("\n");
        processOutputText.setCaretPosition(processOutputText.getDocument().getLength());
    }

    @Override
    public void dispose() {
        if (!stopDownloadAction()) return;
        super.dispose();
    }

    @Override
    protected void applyProperties(ApplicationProperties properties){
        String prefix = "frame.main";

        this.setSize( properties.getInt(prefix + ".width", getWidth()),
                      properties.getInt(prefix + ".height", getHeight()) );

        this.outputPath = properties.getProperty(prefix + ".output_path", "");
        defaultQuality = properties.getProperty(prefix + ".quality", defaultQuality);
        defaultDownloader = properties.getProperty(prefix + ".downloader", defaultDownloader);
        defaultCompatMode = properties.getBool( prefix + ".compatibility", false);
    }

    @Override
    protected void storeProperties(ApplicationProperties properties){
        String prefix = "frame.main";

        properties.put(prefix + ".width", getWidth() );
        properties.put(prefix + ".height", getHeight() );
        properties.put(prefix + ".output_path", outputPath);
        properties.put(prefix + ".quality", qualityComboBox.getSelectedItem());
        properties.put(prefix + ".downloader", downloaderComboBox.getSelectedItem());
        properties.put(prefix + ".compatibility", compatibilityCheckBox.isSelected());
    }

}
