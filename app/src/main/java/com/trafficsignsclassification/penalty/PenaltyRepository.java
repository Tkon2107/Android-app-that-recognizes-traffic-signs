package com.trafficsignsclassification.penalty;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PenaltyRepository {
    private static final String TAG = "PenaltyRepository";
    private static PenaltyRepository instance;
    private Map<String, PenaltyInfo> penaltyMap;
    private Context context;

    private PenaltyRepository(Context context) {
        this.context = context.getApplicationContext();
        this.penaltyMap = new HashMap<>();
    }

    public static synchronized PenaltyRepository getInstance(Context context) {
        if (instance == null) {
            instance = new PenaltyRepository(context);
        }
        return instance;
    }

    public void initialize() {
        try {
            loadPenaltiesFromAssets();
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize penalties", e);
        }
    }

    private void loadPenaltiesFromAssets() {
        try {
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(context.getAssets().open("penalties.json"))
            );
            
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
            reader.close();

            Gson gson = new Gson();
            PenaltyData data = gson.fromJson(json.toString(), PenaltyData.class);

            penaltyMap.clear();
            for (PenaltyEntry entry : data.penalties) {
                PenaltyInfo info = new PenaltyInfo(
                    entry.signLabel,
                    entry.violationName,
                    entry.legalReference,
                    entry.fineRangeCar,
                    entry.fineRangeMotorbike,
                    entry.additionalPenalty,
                    entry.notes,
                    entry.updatedAt,
                    entry.severity
                );
                penaltyMap.put(entry.signLabel.toLowerCase().trim(), info);
            }

            Log.d(TAG, "Loaded " + penaltyMap.size() + " penalties");
        } catch (Exception e) {
            Log.e(TAG, "Error loading penalties", e);
        }
    }

    public PenaltyInfo getPenaltyForLabel(String label) {
        if (label == null) return null;
        
        String normalizedLabel = label.toLowerCase().trim();
        return penaltyMap.get(normalizedLabel);
    }

    // JSON data classes
    private static class PenaltyData {
        @SerializedName("version")
        String version;
        
        @SerializedName("lastUpdated")
        String lastUpdated;
        
        @SerializedName("source")
        String source;
        
        @SerializedName("penalties")
        List<PenaltyEntry> penalties;
    }

    private static class PenaltyEntry {
        @SerializedName("signLabel")
        String signLabel;
        
        @SerializedName("violationName")
        String violationName;
        
        @SerializedName("legalReference")
        String legalReference;
        
        @SerializedName("fineRangeCar")
        String fineRangeCar;
        
        @SerializedName("fineRangeMotorbike")
        String fineRangeMotorbike;
        
        @SerializedName("additionalPenalty")
        String additionalPenalty;
        
        @SerializedName("notes")
        String notes;
        
        @SerializedName("updatedAt")
        String updatedAt;
        
        @SerializedName("severity")
        String severity;
    }
}
