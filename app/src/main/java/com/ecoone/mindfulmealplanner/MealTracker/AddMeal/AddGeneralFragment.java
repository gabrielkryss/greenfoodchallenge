package com.ecoone.mindfulmealplanner.MealTracker.AddMeal;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ecoone.mindfulmealplanner.R;
import com.ecoone.mindfulmealplanner.database.FirebaseDatabaseInterface;
import com.ecoone.mindfulmealplanner.database.Food;
import com.ecoone.mindfulmealplanner.database.Meal;
import com.ecoone.mindfulmealplanner.profile.ProfileFragment;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AddGeneralFragment extends Fragment {
    public AddGeneralFragment(){}


    public static AddGeneralFragment newInstance() {

        Bundle args = new Bundle();

        AddGeneralFragment fragment = new AddGeneralFragment();
        fragment.setArguments(args);
        return fragment;
    }
    private AddGreenMealActivity mCallback;
    private View mainView;
    private Toolbar mToolbar;
    private EditText mealNameEditView;
    private AutoCompleteTextView restaurantNameAutoCompleteTextView;
    private EditText mealDescriptionEditView;
    private RadioGroup mealTypeRadioGroup;
    private CheckBox visibleCheckBox;
    private Button resetButton;
    private Button addPhotoButton;
    private AddGreenMealViewModel mViewModel;
    private LinearLayout addFoodLayout;
    private TextView addFoodLayoutMealTypeTextView;

    private HashMap<String, Boolean> allRestaurantMenu;
    private AutoCompleteRestaurantAdapter mAdapter;

    private Meal mMeal;


    final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    final String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    final FirebaseStorage storage = FirebaseStorage.getInstance();

    private static final String TAG = "testActivity";
    private static final String CLASSTAG = "(AddGreenMealActivity)";
    private static final int REQUEST_FOOD_INFO = 0;

    public interface OnDataPassingListener {
        void finishAddMeal(int input);
    }

    public OnDataPassingListener mOnDataPassingListener;


    public void notifyBackPressed(){
        Meal meal = mViewModel.meal.getValue();
        int count = mViewModel.addedFoodViewList.size()-  meal.foodList.size();
        if(count > 0){
            removeLastFoodView();
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_add_meal_add_general, container, false);
        setHasOptionsMenu(true);
        mViewModel = ViewModelProviders.of(getActivity()).get(AddGreenMealViewModel.class);
        mToolbar = mainView.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
//        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        getActivity().setTitle("Add Meal");

        mealNameEditView = mainView.findViewById(R.id.add_meal_name_editview);
        restaurantNameAutoCompleteTextView = mainView.findViewById(R.id.add_meal_autoCompleteTextView);
        mealDescriptionEditView = mainView.findViewById(R.id.add_meal_description);
        resetButton = mainView.findViewById(R.id.add_meal_reset_button);
        addPhotoButton = mainView.findViewById(R.id.add_photo);
        visibleCheckBox = mainView.findViewById(R.id.add_meal_checkbox);
        mealTypeRadioGroup = mainView.findViewById(R.id.add_meal_rasio_group);

        addFoodLayout = mainView.findViewById(R.id.add_food_layout);
        addFoodLayoutMealTypeTextView = mainView.findViewById(R.id.add_food_layout_meal_type);
        mViewModel.addedFoodViewList = new ArrayList<>();
        allRestaurantMenu = new HashMap<>();
        mMeal = new Meal();
//        photoList
        initializeFragment();
        setupMealObserver();
        return mainView;
    }

    private void setupMealObserver() {
        mViewModel.meal.observe(this, new Observer<Meal>() {
            @Override
            public void onChanged(@Nullable Meal meal) {
                if(meal != null) {
                    mMeal = meal;
                    int index = 0;
                    if(mViewModel.addedFoodViewList.size()>0) {
                        for (String foodName : mMeal.foodList.keySet()) {
                            View view = mViewModel.addedFoodViewList.get(index);
                            ImageView thumbnailImage = view.findViewById(R.id.add_food_thumbnail);
                            TextView foodNameTextView = view.findViewById(R.id.add_food_title);
                            Food food = (Food) meal.foodList.get(foodName);
                            thumbnailImage.setImageBitmap(food.photoBitmap);
                            foodNameTextView.setText(foodName);
                            index++;
                        }
                    }
                }
            }
        });
    }

    public void setCallback(Activity activity){
        mCallback = (AddGreenMealActivity) activity;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.submit,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (isMealBasicInfoCompleted() && mMeal.foodList != null) {
            setMealBasicInfoEditableStatus(0);

            StorageReference storageRef = storage.getReference();
            StorageReference storagePath = storageRef.child("userImage").child(userUid).child(mMeal.mealName);


            ArrayList<Bitmap> photoByteArray = new ArrayList<>();
            ArrayList<String> foodName = new ArrayList<>(mMeal.foodList.keySet());
            int foodNumber = foodName.size();
            for (int i = 0; i < foodNumber; i++) {
                Food food = (Food) mMeal.foodList.get(foodName.get(i));
                photoByteArray.add(food.photoBitmap);
                food.photoBitmap = null;
                food.foodName = null;
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            for (int i = 0; i < foodNumber; i++) {
                Bitmap bitmap = photoByteArray.get(i);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] data = baos.toByteArray();

                UploadTask uploadTask = storagePath.child(foodName.get(i)).putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    }
                });
            }

            FirebaseDatabaseInterface.writeMeal(mMeal);

            mOnDataPassingListener.finishAddMeal(1);

//            setMealInfo();
//                    nextButton.setTextColor(Color.WHITE);
//            FirebaseDatabaseInterface.writeMeal(mMeal);

        }
        else {
            showCustomToast("Please enter the meal information.");
        }
        return true;
    }

    private void initializeFragment() {
        getAllRestaurantMenu().addOnCompleteListener(new OnCompleteListener<HashMap<String, Boolean>>() {
            @Override
            public void onComplete(@NonNull Task<HashMap<String, Boolean>> task) {
                allRestaurantMenu = task.getResult();

                if (allRestaurantMenu != null) {
                    ArrayList<Map.Entry<String, Boolean>> list = new ArrayList<>(allRestaurantMenu.entrySet());
                    mAdapter = new AutoCompleteRestaurantAdapter(getContext(), list);
                    restaurantNameAutoCompleteTextView.setAdapter(mAdapter);
                }
            }
        });

        addPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isMealBasicInfoCompleted()) {
                    setMealInfo();
                    mViewModel.meal.setValue(mMeal);
                    addFoodLayoutMealTypeTextView.setText(mMeal.mealType);
                    addFoodLayout.setVisibility(View.VISIBLE);
                    addNewFoodView();
                    mCallback.switchPage();
                } else {
                    showCustomToast("Please enter the meal information before adding photos.");
                }

            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMealBasicInfoEditableStatus(1);
//                nextButton.setTextColor(getResources().getColor(R.color.button_grey));
                mMeal.clear();
                addFoodLayout.setVisibility(View.GONE);

            }
        });

    }

    private void addNewFoodView() {
        View child = getLayoutInflater().inflate(R.layout.component_add_food_detail, addFoodLayout, false);
        addFoodLayout.addView(child);
        mViewModel.addedFoodViewList.add(child);
    }

    private void removeLastFoodView() {
        int numViews = mViewModel.addedFoodViewList.size();
        if (numViews > 0){
            View view = mViewModel.addedFoodViewList.get(numViews-1);
            mViewModel.addedFoodViewList.remove(numViews - 1);
            ((ViewGroup)view.getParent()).removeView(view);
            numViews--;
        }
        if(numViews == 0){
            addFoodLayout.setVisibility(View.GONE);
        }
    }


    private void setMealInfo() {
        mMeal.mealName = mealNameEditView.getText().toString();
        RadioButton mealTypeButton = mainView.findViewById(mealTypeRadioGroup.getCheckedRadioButtonId());
        mMeal.mealType = mealTypeButton.getText().toString();
        mMeal.restaurantName = restaurantNameAutoCompleteTextView.getText().toString();
        if (mealDescriptionEditView.getText().toString().equals("")) {
            mMeal.mealDescription = "No Description.";
        }
        else {
            mMeal.mealDescription = mealDescriptionEditView.getText().toString();
        }

        mMeal.isPrivate = !visibleCheckBox.isChecked();

        String restaurantName = restaurantNameAutoCompleteTextView.getText().toString();

        if (allRestaurantMenu.containsKey(restaurantName)) {
            if (allRestaurantMenu.get(restaurantName)) {
                mMeal.isGreen = true;
            }
            else {
                mMeal.isGreen = false;
            }
        }
        else {
            mMeal.isGreen = false;
        }

        mMeal.tags.addAll(Arrays.asList(mMeal.mealName, mMeal.mealType, mMeal.restaurantName));

    }

    private boolean isMealBasicInfoCompleted() {
//        Log.i(TAG, CLASSTAG + mealTypeRadioGroup.getCheckedRadioButtonId());
        return !mealNameEditView.getText().toString().equals("") &&
                !restaurantNameAutoCompleteTextView.getText().toString().equals("") &&
                mealTypeRadioGroup.getCheckedRadioButtonId() != -1;
    }



    // sign: 0 uneditable, 1 editable
    private void setMealBasicInfoEditableStatus(int sign) {
        if (sign == 0) {
            mealNameEditView.setEnabled(false);
            restaurantNameAutoCompleteTextView.setEnabled(false);
            mealDescriptionEditView.setEnabled(false);
            for (int i = 0; i < mealTypeRadioGroup.getChildCount(); i++) {
                mealTypeRadioGroup.getChildAt(i).setEnabled(false);
            }
            visibleCheckBox.setEnabled(false);
        }
        else {
            mealNameEditView.setEnabled(true);
            restaurantNameAutoCompleteTextView.setEnabled(true);
            mealDescriptionEditView.setEnabled(true);
            for (int i = 0; i < mealTypeRadioGroup.getChildCount(); i++) {
                mealTypeRadioGroup.getChildAt(i).setEnabled(true);
            }
            visibleCheckBox.setEnabled(true);
        }

    }

//    private void sendMealToFirebase() {
//        Food food1 = new Food();
//        food1.foodName = "testFood1";
//        food1.co2eAmount = 100;
//        food1.ingredient.put("beef", 100);
//        food1.ingredient.put("pork", 10);
//
//
//        Food food2 = new Food();
//        food2.foodName = "testFood2";
//        food2.co2eAmount = 200;
//        food2.ingredient.put("beef", 20);
//        food2.ingredient.put("fish", 120);
//        addFoodToMeal(food1);
//        addFoodToMeal(food2);
//
//        FirebaseDatabaseInterface.writeMeal(mMeal);
//    }
//
//    private void addFoodToMeal(Food food){
//        String foodName = food.foodName;
//        if (mMeal.isGreen) {
//            mMeal.totalCo2eAmount += food.co2eAmount;
//        }
//        food.foodName = null;
//        mMeal.foodList.put(foodName, food);
//    }

    private void showCustomToast(String message) {
        Toast mToast = Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL,
                0, 0);
        mToast.show();
    }


    private Task<HashMap<String, Boolean>> getAllRestaurantMenu() {
        return FirebaseFunctions.getInstance().getHttpsCallable("getAllRestaurantMenu")
                .call().continueWith(new Continuation<HttpsCallableResult, HashMap<String, Boolean>>() {
                    @Override
                    public HashMap<String, Boolean> then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        HashMap<String, Boolean> result = (HashMap<String, Boolean>) task.getResult().getData();
                        return result;
                    }
                });
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mOnDataPassingListener = (AddGeneralFragment.OnDataPassingListener) getContext();
        }catch (ClassCastException e) {
            Log.e(TAG, "onAttach: ClassCastException: " +e.getMessage() + CLASSTAG);
        }
    }


}
