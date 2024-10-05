package org.distributed.mobile_client.ui.home;

import android.R;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.android.material.snackbar.Snackbar;

import org.client.Main;
import org.client.MathTools;
import org.controller.AndroidController;
import org.distributed.mobile_client.databinding.FragmentHomeBinding;
import org.structures.Curve;
import org.structures.Result;

public class HomeFragment extends Fragment {
    private final int PRECISION = 5;
    private MutableLiveData<Result> response = new MutableLiveData<>(null);

    private FragmentHomeBinding binding;


    private class LongRunningTask extends AsyncTask<String, Void, Result> {
        private MutableLiveData<Result> response;

        public LongRunningTask(MutableLiveData<Result> response) {
            this.response = response;
        }

        @Override
        protected Result doInBackground(String... filename) {
            Curve selectedCurve = Main.curves.get(filename[0]);
            AndroidController controller = new AndroidController(HomeFragment.this.getContext(), selectedCurve);
            Result result = controller.processGpx();
            return result;
        }

        @Override
        protected void onPostExecute(Result result) {
            response.postValue(result);
        }
    }



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final Spinner spinner = binding.spinner;

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String file = spinner.getSelectedItem().toString();
                Snackbar.make(view, "Sending workout ... " + file, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                binding.fab.setImageDrawable(getContext().getDrawable(R.drawable.ic_popup_sync));

                LongRunningTask task = new LongRunningTask(response);

                task.execute(file);
            }
        });

        response.observe(this.getViewLifecycleOwner(), new Observer<Result>() {
            @Override
            public void onChanged(Result result) {
                binding.fab.setImageDrawable(getContext().getDrawable(R.drawable.ic_media_play));

                if (result != null) {
                    StringBuffer sb = new StringBuffer();
                    sb.append("Total distance: " + MathTools.roundOff(result.getTotalDistance(),PRECISION) + "\n");
                    sb.append("Total elevation: " + MathTools.roundOff(result.getTotalElevation(),PRECISION) + "\n");
                    sb.append("Total time: " + MathTools.roundOff(result.getTotalTime(),PRECISION) + "\n");
                    sb.append("Average speed:"  + MathTools.roundOff(result.getSpeed(),PRECISION) + "\n");
                    binding.textViewResult.setText(sb.toString());
                } else {
                    binding.textViewResult.setText("");
                }
        }});


        return root;
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}