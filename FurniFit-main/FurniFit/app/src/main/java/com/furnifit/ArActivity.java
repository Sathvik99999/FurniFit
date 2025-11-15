package com.furnifit;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.File;
import java.io.IOException;

import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

public class ArActivity extends AppCompatActivity {

    private ArFragment arFragment;
    private static final String TAG = ArActivity.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;

    @Override
    @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!checkIsSupportedDeviceOrFinish(this)) {
            return;
        }

        setContentView(R.layout.activity_ar);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

        String modelPath = getIntent().getStringExtra("MODEL_NAME");

        loadModelFromFirebase(modelPath);

        arFragment.setOnTapArPlaneListener(
                (hitResult, plane, motionEvent) -> {

                }
        );
    }

    private void loadModelFromFirebase(String modelPath) {
        if (modelPath == null || modelPath.isEmpty()) {
            Toast.makeText(this, "Model path is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- CACHING LOGIC --- 
        // Create a dedicated directory for models in the app's cache
        File cacheDir = new File(getCacheDir(), "models");
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }

        // Create a local file with a name derived from the model path
        String fileName = new File(modelPath).getName();
        File localFile = new File(cacheDir, fileName);

        // Check if the model is already cached
        if (localFile.exists()) {
            Toast.makeText(this, "Loading cached model...", Toast.LENGTH_SHORT).show();
            buildModel(localFile);
        } else {
            Toast.makeText(this, "Downloading model...", Toast.LENGTH_SHORT).show();
            // If not cached, download from Firebase Storage
            StorageReference modelRef = FirebaseStorage.getInstance().getReference().child(modelPath);
            modelRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                buildModel(localFile);
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to download 3D model: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Model download failed", e);
            });
        }
        // --- END OF CACHING LOGIC ---
    }

    private void buildModel(File localFile) {
        ModelRenderable.builder()
                .setSource(this, Uri.fromFile(localFile))
                .setIsFilamentGltf(true)
                .build()
                .thenAccept(modelRenderable -> {
                    Toast.makeText(this, "Model loaded, tap on a plane to place it.", Toast.LENGTH_SHORT).show();

                    arFragment.setOnTapArPlaneListener(
                            (hitResult, plane, motionEvent) -> {
                                Anchor anchor = hitResult.createAnchor();
                                addNodeToScene(anchor, modelRenderable);
                            }
                    );
                })
                .exceptionally(throwable -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(throwable.getMessage()).setTitle("Error loading model");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return null;
                });
    }

    private void addNodeToScene(Anchor anchor, ModelRenderable modelRenderable) {
        AnchorNode anchorNode = new AnchorNode(anchor);
        TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());
        node.setRenderable(modelRenderable);
        node.setParent(anchorNode);
        arFragment.getArSceneView().getScene().addChild(anchorNode);
        node.select();
    }

    public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) {
        String openGlVersionString = ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
                .getDeviceConfigurationInfo()
                .getGlEsVersion();
        if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES 3.0 or later");
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_SHORT).show();
            activity.finish();
            return false;
        }
        return true;
    }
}
