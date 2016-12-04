import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.MalformedURLException;
import java.util.*;

/**
 * Created by manoel on 29/11/2016.
 */
public class AppPlayer {

    private MediaPlayer currentPlayer;
    private Media currentMedia;
    private ImageIcon defaultCover;
    private double currentVolume = 0.2;
    private boolean isPlaying;
    private static boolean replayStatus;
    private List<String> songPathList;
    private Map<String, String> tableToFilePath;
    private JSlider slider;
    private JLabel start;


    /** Initializes the variables and JFX to handle media playback*/
    public AppPlayer(JSlider slider){
        this.slider = slider;
        isPlaying = false;
        replayStatus = false;
        defaultCover = new ImageIcon("res/hm.png");
        songPathList = new ArrayList<>();
        tableToFilePath = new HashMap<>();
        new JFXPanel();
    }

    /** Set a song to the Media object and give it to the player*/
    public void importSongs(File directory, JTable table){

        DefaultTableModel tableModel = (DefaultTableModel)table.getModel();
        tableModel.getDataVector().clear();
        songPathList.clear();
        tableToFilePath.clear();
        songPathList = listSupportedFiles(directory, songPathList);

        int nOfSongs = songPathList.size();
        for(int i = 0; i < nOfSongs; i++){
            Media media = new Media(songPathList.get(i));
            MediaPlayer player = new MediaPlayer(media);
            player.setOnReady(() -> tableModel.addRow(extractMetaData(media)));             //new thread
        }
    }

    /** Starts the song at given index
     * @param index song index to be found and played
     * @param start label to track current time elapsed
     * @param end label to mark the end time of a song */
    public void startSong(String index, JLabel start, JLabel end){
        isPlaying = true;
        this.start = start;
        currentMedia = new Media(tableToFilePath.get(index));
        currentPlayer = new MediaPlayer(currentMedia);
        currentPlayer.setAutoPlay(true);
        slider.setValue(0);
        currentPlayer.setOnReady(() -> {                                //new thread
            end.setText(formatDuration(currentMedia.getDuration()));
            slider.setMaximum((int)currentPlayer.getTotalDuration().toMillis());
            currentPlayer.setVolume(currentVolume);
            final Image cover = getAlbumCover(currentMedia);
            if(cover != null){
                final java.awt.Image resized = SwingFXUtils.fromFXImage(cover, null).getScaledInstance(256, 256, java.awt.Image.SCALE_SMOOTH);
                final ImageIcon convertedCover = new ImageIcon(resized);
                PlayerWindow.label.setIcon(convertedCover);
            }else
                PlayerWindow.label.setIcon(defaultCover);

            currentPlayer.play();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while(currentPlayer.getStatus()!= MediaPlayer.Status.STOPPED){
                        updateTimeLabel(formatDuration(currentPlayer.getCurrentTime()));
                        slider.setValue((int)Math.floor(currentPlayer.getCurrentTime().toMillis()));
                    }
                }
            }).start();
        });
        currentPlayer.setOnEndOfMedia(() ->{
            if(replayStatus)
                startSong(index, start, end);
        });
    }

    /** Resumes the song*/
    public void resumeSong(){
        if(!isPlaying) {
            isPlaying = true;
            currentPlayer.play();
        }
    }
    /** Pauses the song*/
    public void pauseSong(){
        if(isPlaying) {
            isPlaying = false;
            currentPlayer.pause();
        }
    }

    /** Stops the song. (Cannot be resumed)*/
    public void stopSong(){
        if(currentPlayer!=null)
            currentPlayer.stop();
    }

    /** Toggle the music mute status
     * @param status new mute status of this song */
    public void changeMuteStatus(boolean status){
        if(currentMedia!=null)
            currentPlayer.setMute(status);
    }

    /** Set a new current volume to the media playing or paused. Only works if there is a media already set.
     * The volume set here will pass through to the next song.
     * @param volume new current volume*/
    public void setVolume(double volume){
        if(currentMedia != null) {                        //Case there is no music set
            currentPlayer.setVolume(volume);
            currentVolume = volume;
        }
    }

    /** Toggle the replay status for the current song
     * @param status Set replay to On or Off */
    public void enableReplay(boolean status){
        replayStatus = status;
    }

    /**Update the label which tracks current time
     * @param elapsedTime current time */
    public void updateTimeLabel(String elapsedTime){
        final String time = elapsedTime;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                start.setText(time);
            }
        });
    }

    /** Skip to the new time position in the media playing or paused, has no effect the player is in STOPPED state.
     * Invoked when the timer slider has changed
     *  @param position the percentage of the new current time to seek in the media and varies from 0.0 to 1.0*/
    public void skip(double position){
        if(currentMedia!=null){
            currentPlayer.seek(new Duration(position));
        }
    }

    /** Convert the time from M.SS to MM:SS format
     * @param time to be converted
     * @return the formatted time */
    public String formatDuration(Duration time)
    {
        double minutes = time.toMinutes();
        int minutesWhole = (int) Math.floor(minutes);
        int secondsWhole = (int) Math.round((minutes - minutesWhole) * 60);
        return String.format("%1$02d:%2$02d", minutesWhole, secondsWhole);
    }

    /**List files filtered by supported extension
     * @param directory root directory to look for files
     * @return list of songs path from given directory and its subdirectories. */
    public List<String> listSupportedFiles(File directory,List<String> songsList){
        File[] listF = directory.listFiles(new FileExtensionFilter("mp3", "flac"));
        for(File file : listF){
            try {
                songsList.add(file.toURI().toURL().toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        //Look for files in subdirectories
        File[] subDir = directory.listFiles();
        for(File file : subDir){
            if(file.isDirectory())
                listSupportedFiles(file.getAbsoluteFile(), songsList);
        }
        return songsList;
    }

    /** Extract the metadata from the given media
     * @param media to extract the metadata
     * @return An vector of String with the metadata extracted */
    public Vector<String> extractMetaData(Media media){
        String artist = "Desconhecido";         //unknown tag
        String title = "Desconhecido";
        String album = "Desconhecido";
        String genre = "Desconhecido";
        Vector<String> row = new Vector<>();
        // display media's metadata
        for (Map.Entry<String, Object> entry : media.getMetadata().entrySet()){
            switch (entry.getKey()){
                case "title":
                    title = (String)entry.getValue();
                    break;
                case "genre":
                    genre = (String)entry.getValue();
                    break;
                case "album":
                    album = (String)entry.getValue();
                    break;
                case "artist":
                    artist = (String)entry.getValue();
                    break;
            }
        }
        row.add(title);
        row.add(album);
        row.add(artist);
        row.add(genre);

        tableToFilePath.put(title, media.getSource());

        return row;
    }

    public Image getAlbumCover(Media media){
        for (Map.Entry<String, Object> entry : media.getMetadata().entrySet()){
            switch (entry.getKey()){
                    case "image":
                    return (Image)entry.getValue();
            }
        }
        return null;
    }

}
