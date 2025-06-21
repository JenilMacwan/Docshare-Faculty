package com.example.fac.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.fac.R;
import com.example.fac.Sem1;
import com.example.fac.Sem2;
import com.example.fac.Sem3;
import com.example.fac.Sem4;
import com.example.fac.Sem5;
import com.example.fac.Sem6;
import com.example.fac.Sem7;
import com.example.fac.Sem8;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList; // Use ArrayList for easier type casting if needed
import java.util.Calendar;
import java.util.List;

public class HomeFragment extends Fragment {

    // Keep HomeViewModel if you use it elsewhere, otherwise it can be removed for this specific animation
    private HomeViewModel homeViewModel;
    private MaterialButton bt1, bt2;
    // It's slightly better practice to declare the List with the specific type
    private List<MaterialButton> oddSemButtons;
    private List<MaterialButton> evenSemButtons;
    private boolean isOddSemVisible = false;
    private boolean isEvenSemVisible = false;

    // Animation constants
    private static final long ANIMATION_DURATION = 300; // ms
    private static final long STAGGER_DELAY = 50; // ms delay between each button animation
    private static final float TRANSLATION_DISTANCE = 50f; // distance buttons slide (in pixels)

    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        TextView textView = view.findViewById(R.id.text_home);
        textView.setText(getGreeting());

        // Main buttons
        bt1 = view.findViewById(R.id.bt1);
        bt2 = view.findViewById(R.id.bt2);

        // Initialize lists with MaterialButton type
        oddSemButtons = new ArrayList<>();
        oddSemButtons.add(view.findViewById(R.id.bt1_1));
        oddSemButtons.add(view.findViewById(R.id.bt1_2));
        oddSemButtons.add(view.findViewById(R.id.bt1_3));
        oddSemButtons.add(view.findViewById(R.id.bt1_4));

        evenSemButtons = new ArrayList<>();
        evenSemButtons.add(view.findViewById(R.id.bt2_1));
        evenSemButtons.add(view.findViewById(R.id.bt2_2));
        evenSemButtons.add(view.findViewById(R.id.bt2_3));
        evenSemButtons.add(view.findViewById(R.id.bt2_4));

        // --- Click listeners for main buttons ---
        bt1.setOnClickListener(v -> {
            // If Odd are hidden, show them
            if (!isOddSemVisible) {
                // First, hide Even if they are visible
                if (isEvenSemVisible) {
                    animateHideButtons(evenSemButtons);
                    isEvenSemVisible = false;
                }
                // Now, show Odd
                animateShowButtons(oddSemButtons);
                isOddSemVisible = true;
            } else {
                // If Odd are visible, hide them
                animateHideButtons(oddSemButtons);
                isOddSemVisible = false;
            }
        });

        bt2.setOnClickListener(v -> {
            // If Even are hidden, show them
            if (!isEvenSemVisible) {
                // First, hide Odd if they are visible
                if (isOddSemVisible) {
                    animateHideButtons(oddSemButtons);
                    isOddSemVisible = false;
                }
                // Now, show Even
                animateShowButtons(evenSemButtons);
                isEvenSemVisible = true;
            } else {
                // If Even are visible, hide them
                animateHideButtons(evenSemButtons);
                isEvenSemVisible = false;
            }
        });


        setupSemesterButtonClickListeners();


        setButtonsVisibility(oddSemButtons, View.GONE);
        setButtonsVisibility(evenSemButtons, View.GONE);
    }


    private String getGreeting() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour >= 5 && hour < 12) {
            return "Hello, Good Morning, Dear Faculty!";
        } else if (hour >= 12 && hour < 17) {
            return "Hello, Good Afternoon, Dear Faculty!";
        } else {
            return "Hello, Good Evening, Dear Faculty!";
        }
    }
    private void animateShowButtons(List<MaterialButton> buttons) {
        for (int i = 0; i < buttons.size(); i++) {
            MaterialButton button = buttons.get(i);
            Log.d("AnimationDebug", "Showing button: " + button.getId() + ", Current Alpha: " + button.getAlpha() + ", Current TransY: " + button.getTranslationY());
            button.setVisibility(View.VISIBLE);
            button.setAlpha(0f);
            button.setTranslationY(-TRANSLATION_DISTANCE);
            Log.d("AnimationDebug", "Prepared to show button: " + button.getId());

            // Animate to final state
            button.animate()
                    .alpha(1f)
                    .translationY(0f) // Move to original Y position
                    .setDuration(ANIMATION_DURATION)
                    .setStartDelay(i * STAGGER_DELAY) // Apply stagger
                    .setInterpolator(new DecelerateInterpolator()) // Smooth easing out
                    .start();
        }
    }


    private void animateHideButtons(List<MaterialButton> buttons) {
        for (int i = 0; i < buttons.size(); i++) {
            MaterialButton button = buttons.get(i);

            // Don't animate if already hidden or hiding
            if (button.getVisibility() != View.VISIBLE || button.getAnimation() != null && !button.getAnimation().hasEnded()) {
                continue;
            }
            Log.d("AnimationDebug", "Hiding button: " + button.getId() + ", Current Alpha: " + button.getAlpha() + ", Current TransY: " + button.getTranslationY());
            // Animate to hidden state
            button.animate()
                    .alpha(0f)
                    .translationY(TRANSLATION_DISTANCE) // Move down
                    .setDuration(ANIMATION_DURATION)
                    // No stagger needed for hiding usually, but you could add: .setStartDelay(i * STAGGER_DELAY / 2)
                    .setInterpolator(new AccelerateInterpolator()) // Smooth speeding up
                    .withEndAction(() -> {
                        // Reset properties after animation completes
                        Log.d("AnimationDebug", "EndAction Hide for button: " + button.getId() );
                        button.setVisibility(View.GONE);
                        button.setTranslationY(0f); // Reset translation for next show animation
                        button.setAlpha(1f);       // Reset alpha for next show animation
                    })
                    .start();
        }
    }


    private void setButtonsVisibility(List<MaterialButton> buttons, int visibility) {
        for (MaterialButton button : buttons) {
            button.setVisibility(visibility);
        }
    }



    private void setupSemesterButtonClickListeners() {
        oddSemButtons.get(0).setOnClickListener(v -> navigateToSemester(Sem1.class, "Sem1"));
        oddSemButtons.get(1).setOnClickListener(v -> navigateToSemester(Sem3.class, "Sem3"));
        oddSemButtons.get(2).setOnClickListener(v -> navigateToSemester(Sem5.class, "Sem5"));
        oddSemButtons.get(3).setOnClickListener(v -> navigateToSemester(Sem7.class, "Sem7"));

        evenSemButtons.get(0).setOnClickListener(v -> navigateToSemester(Sem2.class, "Sem2"));
        evenSemButtons.get(1).setOnClickListener(v -> navigateToSemester(Sem4.class, "Sem4"));
        evenSemButtons.get(2).setOnClickListener(v -> navigateToSemester(Sem6.class, "Sem6"));
        evenSemButtons.get(3).setOnClickListener(v -> navigateToSemester(Sem8.class, "Sem8"));
    }

    private void navigateToSemester(Class<?> activityClass, String semesterName) {
        if (getContext() != null) {
            Intent intent = new Intent(getContext(), activityClass);
            intent.putExtra("semester", semesterName);
            startActivity(intent);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Cancel any ongoing animations when the view is destroyed
        // to prevent potential memory leaks or crashes
        cancelAnimations(oddSemButtons);
        cancelAnimations(evenSemButtons);
    }

    private void cancelAnimations(List<MaterialButton> buttons) {
        if (buttons != null) {
            for (MaterialButton button : buttons) {
                if (button != null) {
                    button.animate().cancel();
                }
            }
        }
    }
}