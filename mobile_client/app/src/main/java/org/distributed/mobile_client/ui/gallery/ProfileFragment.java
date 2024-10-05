package org.distributed.mobile_client.ui.gallery;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import org.client.MathTools;
import org.controller.AndroidController;
import org.distributed.mobile_client.databinding.FragmentProfileBinding;
import org.structures.Result;

import java.util.HashMap;

public class ProfileFragment extends Fragment {
    private final int PRECISION = 5;
    private MutableLiveData<HashMap<String, Result>> response = new MutableLiveData<>(null);

    private FragmentProfileBinding binding;

    private class LongRunningTask extends AsyncTask<Void, Void, HashMap<String, Result>> {
        private MutableLiveData<HashMap<String, Result>> response;

        public LongRunningTask(MutableLiveData<HashMap<String, Result>> response) {
            this.response = response;
        }

        @Override
        protected HashMap<String, Result> doInBackground(Void... filename) {
            AndroidController controller = new AndroidController(ProfileFragment.this.getContext(), null);
            HashMap<String, Result> result = controller.processProfile();

            return result;
        }

        @Override
        protected void onPostExecute(HashMap<String, Result> result) {
            response.postValue(result);
        }
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        response.observe(this.getViewLifecycleOwner(), new Observer<HashMap<String, Result>>() {
            @Override
            public void onChanged(HashMap<String, Result> result) {
                if (result != null) {
                    StringBuffer sb = new StringBuffer();

                    Result profile = result.get("profile");
                    sb.append("       ~~~ you ~~~ \n");
                    sb.append("Total distance: " + MathTools.roundOff(profile.getTotalDistance(),PRECISION) + "\n");
                    sb.append("Total elevation: " + MathTools.roundOff(profile.getTotalElevation(),PRECISION) + "\n");
                    sb.append("Total time: " + MathTools.roundOff(profile.getTotalTime(),PRECISION) + "\n");
                    sb.append("Average speed:"  + MathTools.roundOff(profile.getSpeed(),PRECISION) + "\n");
                    sb.append("\n");
                    sb.append("  ~~~ community ~~~ \n");
                    Result others = result.get("global_profile");
                    sb.append("Total distance: " + MathTools.roundOff(others.getTotalDistance(),PRECISION) + "\n");
                    sb.append("Total elevation: " + MathTools.roundOff(others.getTotalElevation(),PRECISION) + "\n");
                    sb.append("Total time: " + MathTools.roundOff(others.getTotalTime(),PRECISION) + "\n");
                    sb.append("Average speed:"  + MathTools.roundOff(others.getSpeed(),PRECISION) + "\n");

                    sb.append("\n");
                    sb.append("  ~~~ Performance ~~~ \n");

                    double x1 = MathTools.roundOff(MathTools.percent(profile.getTotalDistance(), others.getTotalDistance()),PRECISION);
                    double x2 = MathTools.roundOff(MathTools.percent(profile.getTotalElevation(), others.getTotalElevation()),PRECISION);
                    double x3 = MathTools.roundOff(MathTools.percent(profile.getTotalTime(), others.getTotalTime()),PRECISION);
                    double x4 = MathTools.roundOff(MathTools.percent(profile.getSpeed(), others.getSpeed()),PRECISION);

                    sb.append("Relative distance: " + x1 + " %\n");
                    sb.append("Relative elevation: " + x2 + " %\n");
                    sb.append("Relative time: " + x3 + "% \n");
                    sb.append("Relative speed:"  + x4 + "% \n");


                    binding.textViewResult.setText(sb.toString());
                } else {
                    binding.textViewResult.setText("");
                }
            }});


        LongRunningTask task = new LongRunningTask(response);

        task.execute();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}