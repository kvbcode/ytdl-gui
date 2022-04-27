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
import com.cyber.ui.swing.HBox;
import com.cyber.util.ApplicationProperties;
import com.cyber.util.RunnableProcess;
import com.cyber.ytdl.VideoDownloader;
import com.cyber.ytdl.VideoDownloaderCommand;
import com.cyber.ytdl.VideoDownloaderSourceFormat;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
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
    protected JComboBox<VideoDownloaderSourceFormat> qualityComboBox;
    protected JComboBox<String> downloaderComboBox;
    protected JComboBox<String> outputPathComboBox;
    protected JButton downloadButton;
    protected JButton browseOutputPathButton;
    protected JCheckBox compatibilityCheckBox;
    protected JCheckBox playlistAllowedCheckBox;
    protected JCheckBox subtitlesAllowedCheckBox;

    protected JProgressBar progressBar;

    protected JTextArea processOutputText;
    protected JScrollPane processOutputScrollPane;

    // Properties
    protected VideoDownloader downloader;
    protected VideoDownloaderCommand defaultTask;

    // Const
    protected static final int OUTPUT_PATH_LIST_LIMIT = 10;
    protected static final String TITLE = "Youtube Downloader GUI";
    protected static final String PROPERTIES_PREFIX = "frame.main";


    public MainFrame(ApplicationProperties properties) {
        super(TITLE, properties);
    }

    @Override
    protected void configure() {
        // Components
        initComponents(root);

        // Layout
        initLayout(root);

        // Actions Listeners
        bindActions();

        // Properties
        applyProperties(properties);
    }

    @Override
    public void initComponents(JPanel root) {

        downloader = new VideoDownloader();
        defaultTask = new VideoDownloaderCommand();

        ImageIcon icon = new ImageIcon(getClass().getResource("/icon.png"));
        setIconImage(icon.getImage());

        qualityComboBox = new JComboBox<>(VideoDownloaderSourceFormat.values());
        downloaderComboBox = new JComboBox<>(VideoDownloader.DOWNLOADER_LIST);
        outputPathComboBox = new JComboBox<>();
        outputPathComboBox.setEditable(true);

        urlLabel = new JLabel("Enter your youtube link here:");
        urlTextField = new JTextField("");
        selectOutputLabel = new JLabel("Save to: ");

        pasteButton = new JButton("Paste");
        browseOutputPathButton = new JButton("Browse...");
        downloadButton = new JButton("Download");
        Font buttonFont = downloadButton.getFont();
        downloadButton.setFont(new Font(buttonFont.getFontName(), Font.BOLD, (int)(buttonFont.getSize()*1.2)));

        compatibilityCheckBox = new JCheckBox("Compatibility mode");
        compatibilityCheckBox.setToolTipText("Set compatibility file format for old devices: (avc+aac).mp4");

        playlistAllowedCheckBox = new JCheckBox("Allow playlist");
        playlistAllowedCheckBox.setToolTipText("Allow whole playlist downloading mode");

        subtitlesAllowedCheckBox = new JCheckBox("Subtitles");
        subtitlesAllowedCheckBox.setToolTipText("Download all subtitles if possible");

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
        root.add(qualityComboBox, BagCell.next().fillX() );
        root.add(downloaderComboBox, BagCell.next().fillX().endRow() );

        // Row 2
        root.add(selectOutputLabel, BagCell.next() );
        root.add(outputPathComboBox, BagCell.next().fillX() );
        root.add(browseOutputPathButton, BagCell.next().fillX() );  // no row end (empty space for downloadButton)

        // Row 3
        root.add(HBox.of(compatibilityCheckBox, playlistAllowedCheckBox, subtitlesAllowedCheckBox),
            BagCell.row(3).alignLeft().width(3));

        root.add(downloadButton, BagCell.next().fillBoth().height(2).endRow() );

        // Row 4
        root.add(progressBar, BagCell.next().fillX().endRow() );

        // Row 5
        root.add(processOutputScrollPane, BagCell.next().fillBoth().weight(1.0, 1.0).endRow() );

        pack();
        
        downloadButton.setMinimumSize(downloadButton.getSize());    // freeze Download button size
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

        browseOutputPathButton.addActionListener(e -> {
            setOutputPath( browseOutputPath( getOutputPath() ) );
        });

        downloader.onDownloadProgressValue(value -> progressBar.setValue(value.intValue()));
        downloader.onDownloadProgressString(progressBar::setString);

        progressBar.addChangeListener(e -> updateProgress());

        downloader.onMessage(this::println);
        downloader.onTermination(this::stopDownloadAction);
        downloader.onComplete(() -> println("SUCCESSFULL COMPLETE"));
        downloader.onError(() -> JOptionPane.showMessageDialog( this,
            "<html><h2>Downloading error</h2>"
            + "An error has occurred. Make sure there is enough disk space.<br>"
            + "Please try downloading again later or select another subroutine<br>"
            + "from the dropdown list. For example <i>yt-dlp</i> or <i>yt-dlp_x86</i>.",
            "Downloading error", JOptionPane.ERROR_MESSAGE)
        );

        JPopupMenu downloaderComboBoxMenu = new JPopupMenu();

        downloaderComboBoxMenu
            .add("Update downloader")
            .addActionListener(e -> updateDownloaderAction());
        
        downloaderComboBox.setComponentPopupMenu(downloaderComboBoxMenu);

        JPopupMenu urlTextFieldMenu = new JPopupMenu();

        urlTextFieldMenu
            .add("Cut")
            .addActionListener(e -> urlTextField.cut());

        urlTextFieldMenu
            .add("Copy")
            .addActionListener(e -> urlTextField.copy());

        urlTextFieldMenu
            .add("Paste")
            .addActionListener(e -> urlTextField.paste());

        urlTextField.setComponentPopupMenu(urlTextFieldMenu);
    }

    public void updateDownloaderAction(){
        prepareProgressUI();
        new Thread(
            new RunnableProcess(downloaderComboBox.getSelectedItem().toString(), "-U")
                .onOutput(this::println)
                .onExit(p -> println("EXIT " + p.exitValue()))
        ).start();
    }

    protected List<String> listComboBox(JComboBox<String> comboBox){
        int count = comboBox.getItemCount();
        List<String> list = new ArrayList<>(count);
        for(int i=0;i<count; i++){
            list.add(comboBox.getItemAt(i));
        }
        return list;
    }

    protected String browseOutputPath(String currentPath){
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

    protected String getOutputPath(){
        return (String)outputPathComboBox.getSelectedItem();
    }

    protected void setOutputPath(String outputPath){
        if (outputPath==null || outputPath.isEmpty()) return;

        if (listComboBox(outputPathComboBox).contains(outputPath))
            outputPathComboBox.removeItem(outputPath);

        outputPathComboBox.insertItemAt(outputPath,0);
        outputPathComboBox.setSelectedItem(outputPath);
    }

    protected void updateProgress(){
        if (downloader.isAlive()){
            progressBar.setVisible(true);
            setTitle("["+progressBar.getValue()+"%] " + TITLE);
        }else{
            progressBar.setVisible(false);
            setTitle(TITLE);
        }
    }

    protected void prepareProgressUI(){
        processOutputText.setText("");
        processOutputScrollPane.scrollRectToVisible(new Rectangle(0,0,0,0));
        progressBar.setValue(0);
        progressBar.setString("");
        updateProgress();
    }

    protected void startDownloadAction(){
        if (urlTextField.getText().isEmpty()) return;
        prepareProgressUI();

        VideoDownloaderCommand vdc = new VideoDownloaderCommand(defaultTask);
        vdc.setUrl(urlTextField.getText());
        vdc.setSourceFormat((VideoDownloaderSourceFormat)qualityComboBox.getSelectedItem());
        vdc.setDownloaderExe(downloaderComboBox.getSelectedItem().toString());
        vdc.setOutputPath(getOutputPath());
        vdc.setCompatibleFormat(compatibilityCheckBox.isSelected());
        vdc.setPlaylistAllowed(playlistAllowedCheckBox.isSelected());
        vdc.setSubtitlesAllowed(subtitlesAllowedCheckBox.isSelected());

        println(vdc.printInfo());

        downloader.execute(vdc.toList());
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
        updateProgress();
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
        String prefix = PROPERTIES_PREFIX;

        this.setSize( properties.getInt(prefix + ".width", getWidth()),
                      properties.getInt(prefix + ".height", getHeight()) );

        defaultTask.setDebug( properties.getBool(prefix + ".debug",
            defaultTask.isDebug()));
        defaultTask.setQuality( properties.getProperty(prefix + ".quality",
            defaultTask.getQuality()));
        defaultTask.setDownloaderExe( properties.getProperty(prefix + ".downloader",
            defaultTask.getDownloaderExe()));
        defaultTask.setCompatibleFormat( properties.getBool(prefix + ".compatibility",
            defaultTask.isCompatibleFormat()));
        defaultTask.setPlaylistAllowed( properties.getBool(prefix + ".allow_playlist",
            defaultTask.isPlaylistAllowed()));
        defaultTask.setSubtitlesAllowed( properties.getBool(prefix + ".allow_subtitles",
            defaultTask.isSubtitlesAllowed()));
        defaultTask.setOutputPath( properties.getProperty(prefix + ".output_path",
            defaultTask.getOutputPath()));
        defaultTask.setFileNamesPattern( properties.getProperty(prefix + ".file_names_pattern",
            defaultTask.getFileNamesPattern()));

        qualityComboBox.setSelectedItem(defaultTask.getSourceFormat());
        downloaderComboBox.setSelectedItem(defaultTask.getDownloaderExe());
        compatibilityCheckBox.setSelected(defaultTask.isCompatibleFormat());
        playlistAllowedCheckBox.setSelected(defaultTask.isPlaylistAllowed());
        subtitlesAllowedCheckBox.setSelected(defaultTask.isSubtitlesAllowed());
        outputPathComboBox.setSelectedItem(defaultTask.getOutputPath());

        properties.getStringList(prefix + ".output_path_list")
            .forEach(str -> outputPathComboBox.addItem(str));

    }

    @Override
    protected void storeProperties(ApplicationProperties properties){
        String prefix = PROPERTIES_PREFIX;

        properties.put(prefix + ".width", getWidth() );
        properties.put(prefix + ".height", getHeight() );
        properties.put(prefix + ".debug", defaultTask.isDebug());
        properties.put(prefix + ".quality", qualityComboBox.getSelectedItem());
        properties.put(prefix + ".downloader", downloaderComboBox.getSelectedItem());
        properties.put(prefix + ".compatibility", compatibilityCheckBox.isSelected());
        properties.put(prefix + ".allow_playlist", playlistAllowedCheckBox.isSelected());
        properties.put(prefix + ".allow_subtitles", subtitlesAllowedCheckBox.isSelected());
        properties.put(prefix + ".file_names_pattern", defaultTask.getFileNamesPattern());

        properties.put(prefix + ".output_path", outputPathComboBox.getSelectedItem());
        properties.putStringList(prefix + ".output_path_list", listComboBox(outputPathComboBox)
            .stream()
            .limit(OUTPUT_PATH_LIST_LIMIT)
            .collect(Collectors.toList())
        );

    }

}
