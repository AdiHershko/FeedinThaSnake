package application;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {
	static int speed=3;
	static int width=20;
	static int height=20;
	static int foodX=0;
	static int foodY=0;
    static int cornersize=25;
	static List<Corner> snake = new ArrayList<>();
	static boolean lose = false;
	static Random rand = new Random();
	static Direction direction=Direction.Left;



	public void start(Stage primaryStage)
	{
		try {

		addFood();
		VBox root = new VBox();
		Canvas c = new Canvas(width*cornersize,height*cornersize);
		GraphicsContext gc = c.getGraphicsContext2D();
		root.getChildren().add(c);

		new AnimationTimer() {
			long lastTick=0;
			public void handle(long now)
			{
				if (lastTick==0) { lastTick=now; tick(gc); return;}
				if (now-lastTick>1000000000/speed) {
					lastTick=now;
					tick(gc);
				}
			}
		}.start();
		Scene scene = new Scene(root,width*cornersize,height*cornersize);
		//keys
		scene.addEventFilter(KeyEvent.KEY_PRESSED, key->{
			if (key.getCode()==KeyCode.W) direction=Direction.Up;
			if (key.getCode()==KeyCode.S) direction=Direction.Down;
			if (key.getCode()==KeyCode.A) direction=Direction.Left;
			if (key.getCode()==KeyCode.D) direction=Direction.Right;
		});
		//snake
		for (int i=0;i<3;i++)
			snake.add(new Corner(width/2,height/2));
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.setTitle("MySnake");
		primaryStage.show();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	//play
	public static void tick(GraphicsContext gc)
	{
		if (lose)
		{
			gc.setFill(Color.RED);
			gc.setFont(new Font("",30));
			gc.fillText("YOU LOSE", 200, 200);
			return;
		}
		for (int i=snake.size()-1;i>0;i--)
		{
			snake.get(i).x=snake.get(i-1).x;
			snake.get(i).y=snake.get(i-1).y;
		}
		switch(direction)
		{
		case Up:
			snake.get(0).y--;
			if (snake.get(0).y<0) lose=true;
			break;
		case Down:
			snake.get(0).y++;
			if (snake.get(0).y>height) lose=true;
			break;
		case Left:
			snake.get(0).x--;
			if (snake.get(0).x<0) lose=true;
			break;
		case Right:
			snake.get(0).x++;
			if (snake.get(0).x>width) lose=true;
			break;
		}
		//food eaten
		if (foodX==snake.get(0).x&&foodY==snake.get(0).y)
		{
			snake.add(new Corner(-1,-1));
			addFood();
		}
		//collide with self
		for (int i = 1 ; i <snake.size();i++)
			if (snake.get(0).x==snake.get(i).x&&snake.get(0).y==snake.get(i).y)
				lose=true;
		//bg
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, width*cornersize, height*cornersize);
		//scoreboard
		gc.setFill(Color.WHITE);
		gc.setFont(new Font("Arial",40));
		gc.fillText("Score: "+(speed-4), 10, 30);
		//paint snake
		for (Corner c : snake) {
			gc.setFill(Color.LIGHTGREEN);
			gc.fillRect(c.x * cornersize, c.y * cornersize, cornersize - 1, cornersize - 1);
			gc.setFill(Color.GREEN);
			gc.fillRect(c.x * cornersize, c.y * cornersize, cornersize - 2, cornersize - 2);

		}
		//paint food
		gc.fillOval(foodX * cornersize, foodY * cornersize, cornersize, cornersize);

	}

	public static void addFood() {
		start: while(true)
		{
			foodX=rand.nextInt(width);
			foodY=rand.nextInt(height);
			for (Corner c : snake)
				if (c.x==foodX&&c.y==foodY) continue start;
			speed++;
			break;

		}
	}

	public static class Corner
	{
		int x,y;
		public Corner(int x,int y)
		{
			this.x=x;
			this.y=y;
		}
	}
	public enum Direction {
		Left,Right,Up,Down
	}
	public static void main(String[] args)
	{
		launch(args);
	}
}
