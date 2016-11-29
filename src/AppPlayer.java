import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

/**
 * Created by manoel on 29/11/2016.
 */
public class AppPlayer {

    private MediaPlayer player;
    private Media media;
    private boolean isPlaying = false;

    public AppPlayer(){
        isPlaying = false;
        new JFXPanel();
    }

    public void setSong(String songPath){
        media = new Media(songPath); //replace /Movies/test.mp3 with your file
        player = new MediaPlayer(media);
    }

    public void resumeSong(){
        if(!isPlaying){
            isPlaying = true;
            player.play();
        }
    }

    public void pauseSong(){
        if(isPlaying) {
            isPlaying = false;
            player.pause();
        }
    }

    public void stopSong(){
        player.stop();
    }

    public void setVolume(double volume){
        player.setVolume(volume);
    }

    public void setCurrentTime(int time){
        //player.set();
    }
}
