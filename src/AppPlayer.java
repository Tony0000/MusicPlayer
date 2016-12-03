import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.net.MalformedURLException;
import java.util.*;

/**
 * Created by manoel on 29/11/2016.
 */
public class AppPlayer {

    private MediaPlayer currentPlayer;
    private Media currentMedia;
    private boolean isPlaying;
    private List<String> songPathList;
    private Map<String, String> tableToFilePath;


    /** Initializes the JFX to handle media playback*/
    public AppPlayer(){
        isPlaying = false;
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
        listSupportedFiles(directory);

        int nOfSongs = songPathList.size();
        for(int i = 0; i < nOfSongs; i++){
            Media media = new Media(songPathList.get(i));
            MediaPlayer player = new MediaPlayer(media);
            player.setOnReady(() -> tableModel.addRow(extractMetaData(media)));             //new thread
        }
    }

    /** Starts the song
     * @param index song index to be found and played
     * @param start label to track current time elapsed
     * @param end label to mark the end time of a song */
    public void startSong(String index, JLabel start, JLabel end){
        isPlaying = true;
        currentMedia = new Media(tableToFilePath.get(index));
        currentPlayer = new MediaPlayer(currentMedia);
        currentPlayer.setAutoPlay(true);
        currentPlayer.setOnReady(() -> {                                //new thread
            end.setText(formatDuration(currentMedia.getDuration()));
            currentPlayer.play();

        });
    }

    /** Pauses the song*/
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

    /** Set to music to mute or unmute if already muted.
     * @param status new mute status of this song */
    public void changeMuteStatus(boolean status){
        if(currentMedia!=null)
            currentPlayer.setMute(status);
    }

    /** Set a new current volume to the media playing or paused. Only works if there is a media already set.
     * @param volume new current volume*/
    public void setVolume(double volume){
        if(currentMedia != null)                        //Case there is no music set
            currentPlayer.setVolume(volume);
    }

    /** Skip to the new time position in the media playing or paused, has no effect the player is in STOPPED state.
     * Invoked when the timer slider has changed
     *  @param position the percentage of the new current time to seek in the media and varies from 0.0 to 1.0*/
    public void skip(double position){
        if(currentMedia!=null){
            double mediaDuration = currentMedia.getDuration().toMillis();
            currentPlayer.seek(new Duration(mediaDuration*position));
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

    public void listSupportedFiles(File directory){
        File[] listF = directory.listFiles(new FileExtensionFilter("mp3", "flac"));
        for(File file : listF){
            try {
                songPathList.add(file.toURI().toURL().toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

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

}
