package application;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Main extends Application {
	final static int WIDTH = 640;
	final static int HEIGHT = 640;

	final static int CURSOR_WIDTH = 32;
	final static int MAX_ITER = 500;

	final static Rectangle[][] image = new Rectangle[WIDTH][HEIGHT];

	@Override
	public void start(Stage primaryStage) {
		StackPane base = new StackPane();
		GridPane root = new GridPane();
		Pane boxPane = new Pane();

		base.getChildren().addAll(root, boxPane);
		Scene scene = new Scene(base, WIDTH, HEIGHT);

		genSet(root);

		primaryStage.setTitle("Mandelbrot");
		primaryStage.setScene(scene);
		primaryStage.show();

		base.requestFocus();
		base.setOnMouseMoved(e -> {
			scene.setCursor(Cursor.NONE);
			boxPane.getChildren().clear();
			Rectangle r = new Rectangle(e.getX() - CURSOR_WIDTH / 2, e.getY() - CURSOR_WIDTH / 2, CURSOR_WIDTH, CURSOR_WIDTH);
			r.setFill(Color.TRANSPARENT);
			r.setStroke(Color.WHITE);
			boxPane.getChildren().addAll(r);
		});

		base.setOnMouseClicked(e -> {
			Point lowerScreen = new Point(e.getX() - CURSOR_WIDTH / 2, e.getY() - CURSOR_WIDTH / 2);
			Point upperScreen = new Point(e.getX() + CURSOR_WIDTH / 2, e.getY() + CURSOR_WIDTH / 2);
			Point lowerComplex = lowerScreen.toComplexNumber();
			Point upperComplex = upperScreen.toComplexNumber();
			Point.hLowerBound = lowerComplex.x;
			Point.hUpperBound = upperComplex.x;
			Point.vLowerBound = lowerComplex.y;
			Point.vUpperBound = upperComplex.y;
			updateSet(root);
		});
	}

	public static void main(String[] args) {
		launch(args);
	}

	public void genSet(GridPane root) {
		for(int i = 0; i < WIDTH; i++) {
			for(int j = 0; j < HEIGHT; j++) {
				Point p = new Point(i, j);
				p = p.toComplexNumber();

				Point temp = new Point(p.x, p.y);
				boolean inSet = true;
				int numIters = 0;
				for(int k = 0; k < MAX_ITER; k++) {
					if(temp.squareMagnitude() >= 4.0) {
						inSet = false;
						numIters = k;
						break;
					}
					temp = temp.square();
					temp = temp.add(p);
				}

				if(inSet)
					image[i][j] = new Rectangle(1, 1, Color.BLACK);
				else
					image[i][j] = new Rectangle(1, 1, Color.hsb(smooth(numIters) * 300, 1, 1));

				root.add(image[i][j], i, j);
			}
		}
	}

	public void updateSet(GridPane root) {
		for(int i = 0; i < WIDTH; i++) {
			for(int j = 0; j < HEIGHT; j++) {
				Point p = new Point(i, j);
				p = p.toComplexNumber();

				Point temp = new Point(p.x, p.y);
				boolean inSet = true;
				int numIters = 0;
				for(int k = 0; k < MAX_ITER; k++) {
					if(temp.squareMagnitude() >= 4.0) {
						inSet = false;
						numIters = k;
						break;
					}
					temp = temp.square();
					temp = temp.add(p);
				}

				if(inSet)
					image[i][j].setFill(Color.BLACK);
				else
					image[i][j].setFill(Color.hsb(smooth(numIters) * 300, 1, 1));
			}
		}
	}
	
	public double smooth(double numIters) {
		return numIters / MAX_ITER;
	}

	static class Point { 
		double x;
		double y;

		static double hLowerBound = -2;
		static double hUpperBound = 0.5;
		static double vLowerBound = -1.25;
		static double vUpperBound = 1.25;

		Point(double x, double y) {
			this.x = x;
			this.y = y;
		}

		//Converts from a range of [0, WIDTH], [0, HEIGHT] to [hLowerBound, hUpperBound], [vLowerBound, vUpperBound]
		Point toComplexNumber() {
			Point p2 = new Point(0, 0);
			p2.x = (hUpperBound - hLowerBound) * x / WIDTH + hLowerBound; 
			p2.y = -((vUpperBound - vLowerBound) * y / HEIGHT + vLowerBound);
			return p2;
		}

		Point add(Point p2) { //GC overhead limit exceeded????
			return new Point(x + p2.x, y + p2.y);
		}


		// (a + bI)(a + bI) = (a^2 - b^2) + 2abI
		Point square() {
			return new Point(x * x - y * y, 2 * x * y);
		}

		double squareMagnitude() {
			return x * x + y * y;
		}

		@Override
		public String toString() {
			return "(" + x + ", " + y + ")";
		}
	}

}


