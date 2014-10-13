package com.tectus.demo.copycat;

import com.tectus.copycat.generator.Dot;
import com.tectus.copycat.generator.DotPattern;
import com.tectus.copycat.generator.DotShape;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

public class App extends Application
{
	private class AppController implements Initializable
	{
		private Pane root;

		/**
		 * UI control binding
		 */
		@FXML private Pane contentPane;
		@FXML private Pane commandPane;
		@FXML private Button generateButton;

		private DotPattern pattern;

		public void start(Stage primaryStage)
		{
			try {
				setUserAgentStylesheet(STYLESHEET_MODENA);

				FXMLLoader loader = new FXMLLoader();

				loader.setLocation(AppController.class.getResource("view/app-shell.fxml"));
				loader.setController(this);
				root = loader.load();
			}
			catch (IOException ex) {
				ex.printStackTrace();
				System.exit(1);
			}

			initContentPane(contentPane);

			primaryStage.setScene(new Scene(root));
			primaryStage.setResizable(true);
			primaryStage.setWidth(400);
			primaryStage.setHeight(550);
			primaryStage.setTitle("MemoDOTS pattern generator");
			primaryStage.show();
		}

		@FXML Group gridBackground;
		@FXML Group levelPattern;
		@FXML Group userPattern;

		protected void initContentPane(Pane parent) {

			byte gridXCount = 8;
			byte gridYCount = 8;

			for (byte col = 0; col < gridXCount; ++col)
				for (byte row = 0; row < gridYCount; ++row) {
					Circle dot = new Circle(col * 40d, row * 40d, 8d);
					gridBackground.getChildren().add(dot);
				}
		}

		@FXML
		public void actionGenerate() {

			levelPattern.setVisible(false);
			levelPattern.getChildren().clear();

			DotPattern pattern = new DotPattern(8, 8);
			pattern.initialize(24, 2, 2, false, false);
			List<DotShape> shapes = pattern.generate();

			// drawing shapes

			for (DotShape shape: shapes) {

				List<Double> shapePoints = new ArrayList<Double>();

				// selecting random color
				String colorStyle = "color" + new Random().nextInt(4);

				// drawing shape's points
				for (Dot dot: shape.getDots()) {
					shapePoints.add(dot.x * 40d);
					shapePoints.add(dot.y * 40d);

					Circle circle = new Circle(dot.x * 40d, dot.y * 40d, 7.5d);
					circle.getStyleClass().add(colorStyle);
					levelPattern.getChildren().add(circle);
				}

				// drawing shape sides
				Polyline shapeFrame = new Polyline();
				shapeFrame.getPoints().addAll(shapePoints);
				shapeFrame.getStyleClass().add(colorStyle);

				if (shape.isClosed()) {

					Dot first = shape.getFirst();
					shapeFrame.getPoints().addAll(first.x * 40d, first.y * 40d);

					Polygon shapeFill = new Polygon();
					shapeFill.getPoints().addAll(shapePoints);
					shapeFill.getStyleClass().add(colorStyle);
					levelPattern.getChildren().add(shapeFill);
				}

				levelPattern.getChildren().add(shapeFrame);
			}

			levelPattern.setVisible(true);
		}

		@Override
		public void initialize(URL location, ResourceBundle resources) {

			/*
			generateButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {

				}
			});
			*/
		}
	}

	@Override
	public void start(Stage primaryStage) {
		new AppController().start(primaryStage);
	}

	public static void main(String[] args) {
		launch(args);
	}
}
