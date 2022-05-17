/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

public class Controller implements Initializable{
	
	@FXML
	private Pane pane;
	@FXML
	private Label mediaLabel;
	@FXML
	private Button playButton, pauseButton, resetButton, previousButton, nextButton;
	@FXML
	private ComboBox<String> speedBox;
	@FXML
	private Slider volumeSlider;
	@FXML
	private ProgressBar mediaProgressBar;
        @FXML
        private MediaView mediaView;
	
	private Media media;
	private MediaPlayer mediaPlayer;
        
	private File directory;
	private File[] files;
	
	private ArrayList<File> videos;
	
	private int mediaNumber;
	private int[] speeds = {20, 40, 60, 80,100, 120, 140, 160,180, 200};
	
	private Timer timer;
	private TimerTask task;
	
	private boolean running;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		videos = new ArrayList<File>();
		
		directory = new File("videos");
		
		files = directory.listFiles();
		
		if(files != null) {
			
			for(File file : files) {
				
				videos.add(file);
			}
		}
		
		media = new Media(videos.get(mediaNumber).toURI().toString());
		mediaPlayer = new MediaPlayer(media);
		
		mediaLabel.setText(videos.get(mediaNumber).getName());
                mediaView.setMediaPlayer(mediaPlayer);
                
		for(int i = 0; i < speeds.length; i++) {
			
			speedBox.getItems().add(Integer.toString(speeds[i])+"%");
		}
		
		speedBox.setOnAction(this::changeSpeed);
		
		volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				
				mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);			
			}
		});
		
		mediaProgressBar.setStyle("-fx-accent: skyblue;");
                mediaPlayer.setOnEndOfMedia(() ->{
                       nextMedia();
                });
                
            /*   mediaProgressBar.valueProperty().addListener((ObservableValue<? extends Number> Observable,Number oldValue,Number newValue)->{
                    if(mediaProgressBar.isPressed()){
                        long duration=new Value.int Value()*1000;
                        mediaPlayer.seek(new Duration(duration));
                    }
                });*/
                
	}

	public void playMedia() {
		
		beginTimer();
		changeSpeed(null);
		mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
                mediaView.setMediaPlayer(mediaPlayer);
		mediaPlayer.play();
                
               
	}
        
        
	 
	public void pauseMedia() {
		
		cancelTimer();
		mediaPlayer.pause();
	}
	
	public void stopMedia() {
		
		mediaPlayer.stop();
	}
	
	public void previousMedia() {
		
		if(mediaNumber > 0) {
			
			mediaNumber--;
			
			mediaPlayer.stop();
			
			if(running) {
				
				cancelTimer();
			}
			
			media = new Media(videos.get(mediaNumber).toURI().toString());
			mediaPlayer = new MediaPlayer(media);
			
			mediaLabel.setText(videos.get(mediaNumber).getName());
			
			playMedia();
		}
		else {
			
			mediaNumber = videos.size() - 1;
			
			mediaPlayer.stop();
			
			if(running) {
				
				cancelTimer();
			}
			
			media = new Media(videos.get(mediaNumber).toURI().toString());
			mediaPlayer = new MediaPlayer(media);
			
			mediaLabel.setText(videos.get(mediaNumber).getName());
			
			playMedia();
		}
	}
	
	public void nextMedia() {
		
		if(mediaNumber < videos.size() - 1) {
			
			mediaNumber++;
			
			mediaPlayer.stop();
			
			if(running) {
				
				cancelTimer();
			}
			
			media = new Media(videos.get(mediaNumber).toURI().toString());
			mediaPlayer = new MediaPlayer(media);
			
			mediaLabel.setText(videos.get(mediaNumber).getName());
			
			playMedia();
		}
		else {
			
			mediaNumber = 0;
			
			mediaPlayer.stop();
			
			media = new Media(videos.get(mediaNumber).toURI().toString());
			mediaPlayer = new MediaPlayer(media);
			
			mediaLabel.setText(videos.get(mediaNumber).getName());
			
			playMedia();
		}
	}
	
	public void changeSpeed(ActionEvent event) {
		
		if(speedBox.getValue() == null) {
			
			mediaPlayer.setRate(1);
		}
		else {
			
			//mediaPlayer.setRate(Integer.parseInt(speedBox.getValue()) * 0.01);
			mediaPlayer.setRate(Integer.parseInt(speedBox.getValue().substring(0, speedBox.getValue().length() - 1)) * 0.01);
		}
	}
	
	public void beginTimer() {
		
		timer = new Timer();
		
		task = new TimerTask() {
			
                        @Override
			public void run() {
				
				running = true;
				double current = mediaPlayer.getCurrentTime().toSeconds();
				double end = media.getDuration().toSeconds();
				mediaProgressBar.setProgress(current/end);
				
				if(current/end == 1) {
					
					cancelTimer();
				}
			}
		};
		
		timer.scheduleAtFixedRate(task, 0, 1000);
	}
	
	public void cancelTimer() {
		
		running = false;
		timer.cancel();
	}
}