package com.trafficsignsclassification.penalty;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.trafficsignsclassification.R;
import com.trafficsignsclassification.databinding.ActivityPenaltyDetailBinding;

public class PenaltyDetailActivity extends AppCompatActivity {

    public static final String EXTRA_SIGN_LABEL = "sign_label";
    
    private ActivityPenaltyDetailBinding binding;
    private PenaltyRepository penaltyRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPenaltyDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        penaltyRepository = PenaltyRepository.getInstance(this);

        String signLabel = getIntent().getStringExtra(EXTRA_SIGN_LABEL);

        if (signLabel != null) {
            loadPenaltyInfo(signLabel);
        } else {
            Toast.makeText(this, "Không có thông tin biển báo", Toast.LENGTH_SHORT).show();
            finish();
        }

        binding.closeButton.setOnClickListener(v -> finish());
    }

    private void loadPenaltyInfo(String signLabel) {
        PenaltyInfo penaltyInfo = penaltyRepository.getPenaltyForLabel(signLabel);

        if (penaltyInfo != null) {
            displayPenaltyInfo(penaltyInfo);
        } else {
            displayNoPenaltyInfo(signLabel);
        }
    }

    private void displayPenaltyInfo(PenaltyInfo info) {
        binding.signNameTextView.setText(info.getSignLabel());
        binding.severityTextView.setText("Mức độ: " + info.getSeverityDisplay());
        binding.severityTextView.setTextColor(info.getSeverityColor());
        
        binding.violationNameTextView.setText(info.getViolationName());
        binding.carFineTextView.setText(info.getFineRangeCar());
        binding.motorbikeFineTextView.setText(info.getFineRangeMotorbike());
        binding.legalReferenceTextView.setText(info.getLegalReference());
        binding.lastUpdatedTextView.setText("Cập nhật: " + info.getUpdatedAt());

        // Show/hide additional penalty
        if (info.getAdditionalPenalty() != null && !info.getAdditionalPenalty().isEmpty()) {
            binding.additionalPenaltyCard.setVisibility(View.VISIBLE);
            binding.additionalPenaltyTextView.setText(info.getAdditionalPenalty());
        } else {
            binding.additionalPenaltyCard.setVisibility(View.GONE);
        }

        // Show/hide notes
        if (info.getNotes() != null && !info.getNotes().isEmpty()) {
            binding.notesCard.setVisibility(View.VISIBLE);
            binding.notesTextView.setText(info.getNotes());
        } else {
            binding.notesCard.setVisibility(View.GONE);
        }
    }

    private void displayNoPenaltyInfo(String signLabel) {
        binding.signNameTextView.setText(signLabel);
        binding.severityTextView.setText("Mức độ: Không xác định");
        binding.severityTextView.setTextColor(android.graphics.Color.parseColor("#757575"));
        
        binding.violationNameTextView.setText("Không có thông tin vi phạm");
        binding.carFineTextView.setText("Chưa có dữ liệu");
        binding.motorbikeFineTextView.setText("Chưa có dữ liệu");
        binding.legalReferenceTextView.setText("N/A");
        binding.lastUpdatedTextView.setText("Chưa cập nhật");
        
        binding.additionalPenaltyCard.setVisibility(View.GONE);
        binding.notesCard.setVisibility(View.GONE);
    }
}
