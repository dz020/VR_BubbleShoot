package com.example.jackson.gameuebung_3.game;

import android.content.Context;
import android.graphics.Typeface;
import android.opengl.GLES20;
import android.util.Log;

import com.example.jackson.gameuebung_3.MGDExerciseActivity;
import com.example.jackson.gameuebung_3.Mesh;
import com.example.jackson.gameuebung_3.graphics.Camera;
import com.example.jackson.gameuebung_3.graphics.CompareFunction;
import com.example.jackson.gameuebung_3.graphics.Material;
import com.example.jackson.gameuebung_3.graphics.Renderer;
import com.example.jackson.gameuebung_3.graphics.SpriteFont;
import com.example.jackson.gameuebung_3.graphics.TextBuffer;
import com.example.jackson.gameuebung_3.graphics.Texture;
import com.example.jackson.gameuebung_3.math.Matrix4x4;
import com.example.jackson.gameuebung_3.math.Vector3;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by Jackson on 29.03.2016.
 */
public class MGDExerciseGame extends Game{
    public static String TAG = "MGDExerciseGame";

    private Camera camera;
    public static LinkedList<GameObject> unvisible_gameObjectList = new LinkedList<>();
    LinkedList<GameObject> currently_visible_gameObjects = new LinkedList<>();
    public static GameState gameState;
    private Renderer renderer;
    public static Context context;
    private Vector3 forwardVector = new Vector3();
    private Matrix4x4 headView;

    public static TextBuffer scoreText;
    Matrix4x4 scoreMatrix;
    TextBuffer scoreLabelText;
    Matrix4x4 scoreLabelMatrix;
    public static TextBuffer timeText;
    Matrix4x4 timeMatrix;
    TextBuffer timeLabelText;
    Matrix4x4 timeLabelMatrix;
    public static TextBuffer amorText;
    Matrix4x4 amorMatrix;

    public static TextBuffer currentHighScoreText;
    Matrix4x4 currentHighScoreMatrix;
    public static TextBuffer currentHighScoreP1Text;
    Matrix4x4 currentHighScoreP1Matrix;
    public static TextBuffer currentHighScoreP2Text;
    Matrix4x4 currentHighScoreP2Matrix;
    public static TextBuffer currentHighScoreP3Text;
    Matrix4x4 currentHighScoreP3Matrix;
    public static TextBuffer currentHighScoreP4Text;
    Matrix4x4 currentHighScoreP4Matrix;
    public static TextBuffer currentHighScoreP5Text;
    Matrix4x4 currentHighScoreP5Matrix;

    Matrix4x4 munitionsBoxMatrix;

    public MGDExerciseGame(Context context) {
        super(context);
        this.context = context;
    }

    GameObject fadenkreuz;
    GameObject highscore_background;
    GameObject highscore_btn_play_again;
    GameObject munitions_box;

    Matrix4x4 adjustToCenterMatrix;

    Mesh skyMesh;
    Texture skyTexture;
    Material skyMaterial;
    Matrix4x4 skyMatrix;

    List<List<String>> list;


    @Override
    public void initialize() {
        gameState = new GameState();
        gameState.level = 1;
        createWorld();

        try {
            //list anlegen mit allen möglichen gameobject positionen
            list = UtilityMethods.getListFromCSV("really_final_object_list.csv");
            for(int i = 0; i< list.size(); i++){
                if(i == 7){
                    munitions_box = new GameObject("box.obj", "munition_texture.png");
                    munitions_box.addData(list.get(i));
                }else{
                    GameObject gameObject = new GameObject("box.obj", "box.png");
                    gameObject.addData(list.get(i));
                    unvisible_gameObjectList.add(gameObject);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        skyMesh = UtilityMethods.loadMesh("sphere.obj");
        skyTexture = UtilityMethods.loadTexture("spacetexture.jpg");
        skyMaterial = new Material();
        skyMaterial.setTexture(skyTexture);
        skyMaterial.setDepthTestFunction(CompareFunction.ALWAYS);
        skyMatrix = new Matrix4x4();
        skyMatrix.scale(100f);

        fadenkreuz = new GameObject("quad.obj", "fadenkreuz.png");
        fadenkreuz.makeShapeVisible("sphere.obj", "yellow.png");

        highscore_background = new GameObject("quad.obj", "highscore_bg.png");
        highscore_btn_play_again = new GameObject("quad.obj", "again_btn.png");

        Matrix4x4 tmp = Matrix4x4.createTranslation(-0.5f, 0.5f, -8f);
        Matrix4x4 test = new Matrix4x4(tmp);
        highscore_background.setPosition_in_world(tmp.scale(6,10,1));
        highscore_btn_play_again.setPosition_in_world(test.scale(1.5f, 0.7f, 1.0f ).translate(0f, 1f, 0f));
        currentHighScoreMatrix = new Matrix4x4(highscore_btn_play_again.getGameObjectPositionInWorldMatrix());
        currentHighScoreMatrix.translate(-1.5f, 1.4f, 0f).scale(0.005f, 0.01f, 0.01f);

        currentHighScoreP1Matrix = new Matrix4x4(highscore_btn_play_again.getGameObjectPositionInWorldMatrix());
        currentHighScoreP1Matrix.translate(-0.65f, -3f, 0f).scale(0.005f, 0.01f, 0.01f);
        currentHighScoreP2Matrix = new Matrix4x4(highscore_btn_play_again.getGameObjectPositionInWorldMatrix());
        currentHighScoreP2Matrix.translate(-0.65f, -4f, 0f).scale(0.005f, 0.01f, 0.01f);
        currentHighScoreP3Matrix = new Matrix4x4(highscore_btn_play_again.getGameObjectPositionInWorldMatrix());
        currentHighScoreP3Matrix.translate(-0.65f, -5f, 0f).scale(0.005f, 0.01f, 0.01f);
        currentHighScoreP4Matrix = new Matrix4x4(highscore_btn_play_again.getGameObjectPositionInWorldMatrix());
        currentHighScoreP4Matrix.translate(-0.65f, -6f, 0f).scale(0.005f, 0.01f, 0.01f);
        currentHighScoreP5Matrix = new Matrix4x4(highscore_btn_play_again.getGameObjectPositionInWorldMatrix());
        currentHighScoreP5Matrix.translate(-0.65f, -7f, 0f).scale(0.005f, 0.01f, 0.01f);

        munitions_box.setType("munitions_box");
        munitionsBoxMatrix = munitions_box.getGameObjectPositionInWorldMatrix();
        currently_visible_gameObjects.add(munitions_box);

        Typeface myTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/rubik.ttf");
        SpriteFont spriteFont = new SpriteFont(graphicsDevice, myTypeface, 44f);

        scoreText = graphicsDevice.createTextBuffer(spriteFont, 16);
        scoreText.setText("0");
        scoreLabelText = graphicsDevice.createTextBuffer(spriteFont, 16);
        scoreLabelText.setText("SCORE");

        timeText = graphicsDevice.createTextBuffer(spriteFont, 16);
        timeLabelText = graphicsDevice.createTextBuffer(spriteFont, 16);
        timeLabelText.setText("TIME");

        amorText = graphicsDevice.createTextBuffer(spriteFont, 16);
        amorText.setText(" I I I I I I I I");

        currentHighScoreText = graphicsDevice.createTextBuffer(spriteFont, 16);
        currentHighScoreP1Text = graphicsDevice.createTextBuffer(spriteFont, 16);
        currentHighScoreP2Text = graphicsDevice.createTextBuffer(spriteFont, 16);
        currentHighScoreP3Text = graphicsDevice.createTextBuffer(spriteFont, 16);
        currentHighScoreP4Text = graphicsDevice.createTextBuffer(spriteFont, 16);
        currentHighScoreP5Text = graphicsDevice.createTextBuffer(spriteFont, 16);

        renderer = new Renderer(graphicsDevice);

        MGDExerciseActivity.startNewInGamePollTask();
        while(MGDExerciseActivity.soundPoolLoadingFinished == false){

        }
        MGDExerciseActivity.gameIsReady = true;
        //hier könnte dann gamelevel inkrementiert werden und mit gameobject amount multipliziert werden
        gameState.setStatus("in game");
    }

    public void setForwardVector(Vector3 forwardVector) {
        this.forwardVector = forwardVector;
    }

    boolean first_update_happened = false;
    @Override
    public void update(float deltaSeconds) {

        Matrix4x4 fadenkreuzWorldMatrix = Matrix4x4.multiply(headView.getInverse(), Matrix4x4.createTranslation(-0.5f, 0.5f, -8f));
        adjustToCenterMatrix = new Matrix4x4(fadenkreuzWorldMatrix);
        adjustToCenterMatrix.translate(0.25f,-0.85f, 0f); //sehr angenehm fürs auge positioniert
        fadenkreuz.setPosition_in_world(adjustToCenterMatrix);

            Matrix4x4 fadenkreuzMatrixCopyForScoreLabelMatrix = new Matrix4x4(fadenkreuzWorldMatrix);
            fadenkreuzMatrixCopyForScoreLabelMatrix.rotateX(0);
            fadenkreuzMatrixCopyForScoreLabelMatrix.scale(0.0105f);
            fadenkreuzMatrixCopyForScoreLabelMatrix.translate(-170f, 50f, 0f);
            scoreLabelMatrix = fadenkreuzMatrixCopyForScoreLabelMatrix;

            Matrix4x4 fadenkreuzMatrixCopyForScoreMatrix = new Matrix4x4(fadenkreuzWorldMatrix);
            fadenkreuzMatrixCopyForScoreMatrix.rotateX(0);
            fadenkreuzMatrixCopyForScoreMatrix.scale(0.0105f);
            fadenkreuzMatrixCopyForScoreMatrix.translate(-170f, 7f, 0f);
            scoreMatrix = fadenkreuzMatrixCopyForScoreMatrix;

        Matrix4x4 fadenkreuzMatrixCopyForTimeLabelMatrix = new Matrix4x4(fadenkreuzWorldMatrix);
        fadenkreuzMatrixCopyForTimeLabelMatrix.rotateX(0);
        fadenkreuzMatrixCopyForTimeLabelMatrix.scale(0.0105f);
        fadenkreuzMatrixCopyForTimeLabelMatrix.translate(50f, 50f, 0f);
        timeLabelMatrix = fadenkreuzMatrixCopyForTimeLabelMatrix;

        Matrix4x4 fadenkreuzMatrixCopyForTimeMatrix = new Matrix4x4(fadenkreuzWorldMatrix);
        fadenkreuzMatrixCopyForTimeMatrix.rotateX(0);
        fadenkreuzMatrixCopyForTimeMatrix.scale(0.0105f);
        fadenkreuzMatrixCopyForTimeMatrix.translate(50f, 7f, 0f);
        timeMatrix = fadenkreuzMatrixCopyForTimeMatrix;

            Matrix4x4 fadenkreuzMatrixCopyForAmorMatrix = new Matrix4x4(fadenkreuzWorldMatrix);
            fadenkreuzMatrixCopyForAmorMatrix.rotateX(0);
            fadenkreuzMatrixCopyForAmorMatrix.scale(0.02f);
            fadenkreuzMatrixCopyForAmorMatrix.translate(-90f, -120f, 0f);
            amorMatrix = fadenkreuzMatrixCopyForAmorMatrix;

        if(gameState.status.equals("menu before game")){

        }

        if(gameState.status.equals("in game")) { //also wenn in game
            Log.e(TAG, "in game -----");
            int counter = 0;
            //gucken dass immer 3 boxen zum abschießen da sind
            while(currently_visible_gameObjects.size() < gameState.max_game_object_amount){

                Random randomGenerator = new Random();
                int index = randomGenerator.nextInt(unvisible_gameObjectList.size());
                //Log.e("index", "index werte: "+index);

                if(currently_visible_gameObjects.size() == 0){
                    currently_visible_gameObjects.add(unvisible_gameObjectList.get(index));
                    counter++;
                    break;
                }

                if( (unvisible_gameObjectList.get(index).orbit != currently_visible_gameObjects.get(counter).orbit) &&
                        (unvisible_gameObjectList.get(index).slot != currently_visible_gameObjects.get(counter).slot) ) {
                    ///Log.e("in if", "mit index: "+index);
                    if( !(unvisible_gameObjectList.get(index).getShape().intersects(currently_visible_gameObjects.get(counter).getShape(), currently_visible_gameObjects.get(counter).getOrbit())) ) {
                        currently_visible_gameObjects.add(unvisible_gameObjectList.get(index));
                        counter++;
                        continue;
                    }
                }
            }
            //Log.e("visible", "_game_objects should be: "+currently_visible_gameObjects.size());

            //munition herunterzählen unabhängig von collisions
            if (MGDExerciseActivity.noise_deteced == true) {
                Log.e("increment", "current ammo "+ gameState.current_ammo);
                String amor = "";
                for (int ii = 0; ii < gameState.current_ammo; ii++) {
                    amor += " I";
                }
                amorText.setText(amor);
                //Log.e("amor II ", amor);
            }

            //collision detection
            for (int i = 0; i < currently_visible_gameObjects.size(); i++) {
                if (fadenkreuz.getShape().intersects(currently_visible_gameObjects.get(i).getShape(), currently_visible_gameObjects.get(i).getOrbit())) {
                    Log.e(TAG, "collsion detected !!!!!");
                    if (MGDExerciseActivity.noise_deteced == true) { //schuss ausgelöst
                        if (currently_visible_gameObjects.get(i).getType().equals("munitions_box")) {
                            Log.e("munitionsbox", "sollte abgeschossen worden sein");
                            MGDExerciseActivity.setCollision(true, true);
                            gameState.current_ammo = gameState.current_ammo + gameState.increase_ammo; //aktuell um 3 erhöhen
                            if (gameState.current_ammo > gameState.max_ammo) {
                                gameState.current_ammo = gameState.max_ammo;
                            }
                            Log.e("neue munition", " :"+gameState.current_ammo );
                            currently_visible_gameObjects.remove(i);
                            munitions_box = new GameObject("box.obj", "munition_texture.png");
                            munitions_box.setPosition_in_world(munitionsBoxMatrix);
                            munitions_box.setType("munitions_box");
                            currently_visible_gameObjects.add(munitions_box);
                        }
                        else{
                            if(!gameState.empty_ammo){
                                Log.e(TAG, "normale box sollte abgeschossen worden sein");
                                MGDExerciseActivity.setCollision(true, false);
                                //currently_visible_gameObjects.get(i).setDestroyed(); //färbt boxen rot statt sie zu entfernen, für debugging
                                gameState.setCurrent_score(gameState.getCurrent_score() + currently_visible_gameObjects.get(i).points);
                                scoreText.setText("" + (int) gameState.getCurrent_score());
                                Log.e("aktueller score:", "" + gameState.getCurrent_score());
                                currently_visible_gameObjects.remove(i);
                            }
                        }
                    }
                    else {
                        //gameObjectList.get(i).setModelTexture("box.png");
                        MGDExerciseActivity.setCollision(false, false);
                    }
                }
            }
        }if(gameState.status.equals("game over")){

            if( fadenkreuz.getShape().intersects(highscore_btn_play_again.getShape(), 0.5f )){
                highscore_btn_play_again.setModelTexture("again_btn_hovered.png");
                if(MGDExerciseActivity.noise_deteced){
                    Log.e(TAG, "game soll neu gestartet werden");
                    gameState.resetAmmo();
                    gameState.resetHighscore();
                    MGDExerciseActivity.startNewInGamePollTask();
                    gameState.setStatus("in game");
                    first_update_happened = false;
                }
            }else{
                highscore_btn_play_again.setModelTexture("again_btn.png");
            }
        }
        if(!first_update_happened){
            MGDExerciseActivity.resetGameDuration(); //startet die zeit nach dem ersten update call
            first_update_happened = true;
        }
    }

    @Override
    public void draw(float deltaSeconds) {
        graphicsDevice.setCamera(this.camera);

        graphicsDevice.clear(1.0f, 0.5f, 0.0f, 1.0f, 1.0f); //hintergrund farbe ändern

        renderer.drawMesh(skyMesh, skyMaterial, skyMatrix);

        GLES20.glClearDepthf(1.0f);
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT);

        if(gameState.status.equals("menu before game")){

        }

        if(gameState.status.equals("in game")){

            for(int i=0; i< currently_visible_gameObjects.size(); i++){
                GameObject gameObjectTmp = currently_visible_gameObjects.get(i);
                //if(gameObject.destroyed == false){
                gameObjectTmp.getGameObjectPositionInWorldMatrix().rotateY(1f);
                renderer.drawMesh(gameObjectTmp.getModelMesh(), gameObjectTmp.getModelMaterial(), gameObjectTmp.getGameObjectPositionInWorldMatrix());
//                renderer.drawMesh(gameObjectTmp.shape.getMesh(), gameObjectTmp.shape.getMaterial(), gameObjectTmp.getGameObjectPositionInWorldMatrix());
                //}
                //Log.e("posis", "winkel: "+gameObjectTmp.slot+" orbit: "+gameObjectTmp.orbit);
            }
            //Log.e("draw call", "es sollten gameobjects gezeichnet werden: "+currently_visible_gameObjects.size());

            munitions_box.getGameObjectPositionInWorldMatrix().rotateY(0.1f);
            renderer.drawMesh(munitions_box.getModelMesh(), munitions_box.getModelMaterial(), munitions_box.getGameObjectPositionInWorldMatrix());

            renderer.drawMesh(fadenkreuz.getModelMesh(), fadenkreuz.getModelMaterial(), fadenkreuz.getGameObjectPositionInWorldMatrix());
            renderer.drawText(scoreLabelText, scoreLabelMatrix);
            renderer.drawText(scoreText, scoreMatrix);
            renderer.drawText(timeLabelText, timeLabelMatrix);
            renderer.drawText(timeText, timeMatrix);
            renderer.drawText(amorText, amorMatrix);
        }
        if(gameState.status.equals("game over")){
            renderer.drawMesh(highscore_background.getModelMesh(), highscore_background.getModelMaterial(), highscore_background.getGameObjectPositionInWorldMatrix());
            renderer.drawText(currentHighScoreText, currentHighScoreMatrix);
            renderer.drawText(currentHighScoreP1Text, currentHighScoreP1Matrix);
            renderer.drawText(currentHighScoreP2Text, currentHighScoreP2Matrix);
            renderer.drawText(currentHighScoreP3Text, currentHighScoreP3Matrix);
            renderer.drawText(currentHighScoreP4Text, currentHighScoreP4Matrix);
            renderer.drawText(currentHighScoreP5Text, currentHighScoreP5Matrix);
            renderer.drawMesh(highscore_btn_play_again.getModelMesh(), highscore_btn_play_again.getModelMaterial(), highscore_btn_play_again.getGameObjectPositionInWorldMatrix());
            renderer.drawMesh(fadenkreuz.getModelMesh(), fadenkreuz.getModelMaterial(), fadenkreuz.getGameObjectPositionInWorldMatrix());
        }
    }

    @Override
    public void resize(int width, int height) {
        //ständig bildschirm seitenverhältnis ausrechnen
        float aspect_ratio = ((float) width)/ ((float) height); //aspect ratio = seitenverhältnis
        Matrix4x4 projectionMatrix = new Matrix4x4();
        projectionMatrix.setPerspectiveProjection(0.1f, 0.1f * aspect_ratio, -0.1f, -0.1f * aspect_ratio, 0.1f, 1000.0f); //wenn handy gedreht, dann wird bildschirmausschnitt breiter um aspect ratio
        camera.setM_projection(projectionMatrix);
    }

    @Override
    public void loadContent() {
        //TODO texture neu laden wenn app aus hintergrund wieder in den vordergrund kommt
    }

    public void setCameraParameters(float[] perspective, float[] eyeView) {
        camera.setM_projection(new Matrix4x4(perspective));
        camera.setM_view(new Matrix4x4(eyeView));
    }

    public void createWorld(){
        camera = new Camera();
        Matrix4x4 projectionMatrix = new Matrix4x4();
        projectionMatrix.setPerspectiveProjection(0.1f, 0.1f, -0.1f, -0.1f, 0.1f, 100.0f); //wird in resize überschrieben
        camera.setM_projection(projectionMatrix);
        Matrix4x4 viewMatrix = new Matrix4x4();
        viewMatrix.translate(0.0f, 10.0f, -30.0f);
        camera.setM_view(viewMatrix);
    }

    public void setHeadView(Matrix4x4 headView) {
        this.headView = headView;
        forwardVector.setX(headView.m[2]);
        forwardVector.setY(headView.m[6]);
        forwardVector.setZ(headView.m[10]);
    }

    public static void setTimeText(String text){
        timeText.setText(text);
    }
}
