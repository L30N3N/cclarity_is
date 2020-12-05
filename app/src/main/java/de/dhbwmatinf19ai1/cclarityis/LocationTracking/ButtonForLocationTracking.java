package de.dhbwmatinf19ai1.cclarityis.LocationTracking;
//TODO LOCATIONSERVICE GET DATA WITH BROADCASTRECIEVER
import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;

import de.dhbwmatinf19ai1.cclarityis.R;
import de.dhbwmatinf19ai1.cclarityis.databinding.FragmentButtonForLocationTrackingBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ButtonForLocationTracking#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ButtonForLocationTracking extends Fragment implements View.OnClickListener {

    //View Binding
    private FragmentButtonForLocationTrackingBinding binding;

    //LastKnown Service
    private FusedLocationProviderClient fusedLocationClient;
    private String Ergebnis = "null";

    //LocationService
    private LocationService MyLocationService;
    private static final int REQUESTR_CODE_LOCATION_PERMISSION = 1;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ButtonForLocationTracking() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ButtonForLocationTracking.
     */
    // TODO: Rename and change types and number of parameters
    public static ButtonForLocationTracking newInstance(String param1, String param2) {
        ButtonForLocationTracking fragment = new ButtonForLocationTracking();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_button_for_location_tracking, container, false);
    }

///////////////////////////////////////////////////////

    @Override
    public void onClick(View view) {

        switch(view.getId()){
            case R.id.btnLocationSTART:
                if(ContextCompat.checkSelfPermission(
                        getActivity(), Manifest.permission.ACCESS_FINE_LOCATION
                )!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(
                            getActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUESTR_CODE_LOCATION_PERMISSION
                    );
                }else{
                    startLocationService();
                }
                break;
            case R.id.btnLocationSOPT:
                stopLocationService();
                break;
            case R.id.btn_test:
                getLocation();

            default:
                break;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions,grantResults);
        if(requestCode== REQUESTR_CODE_LOCATION_PERMISSION && grantResults.length >0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startLocationService();
            }else{
                Toast.makeText(getActivity(), "Permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private boolean isLocationServiceRunning(){
        ActivityManager activityManager =
                (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        if(activityManager != null){
            for(ActivityManager.RunningServiceInfo service:
                    activityManager.getRunningServices(Integer.MAX_VALUE)){
                if(LocationService.class.getName().equals(service.service.getClassName())){
                    if(service.foreground){
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }

    private void startLocationService(){
        if(!isLocationServiceRunning()){
            Intent intent = new Intent(getActivity(),LocationService.class); //getApplicationContext() in Activity
            intent.setAction(Constants.ACTION_START_LOCATION_SERVICE);
            getActivity().startService(intent);
            Toast.makeText(getActivity(),"Location service started", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopLocationService(){
        if(isLocationServiceRunning()){
            Intent intent = new Intent(getActivity(),LocationService.class);
            intent.setAction(Constants.ACTION_STOP_LOCATION_SERVICE);
            getActivity().startService(intent);
            Toast.makeText(getActivity(),"Location service stopped", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getActivity(),"Location service wurde bereitrs gestoppt", Toast.LENGTH_SHORT).show();
        }
    }

    public void getLocation(){
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            maketoast(location.toString());
                        }
                    }
                });
        //TODO Achtung bug Ergebnis wird sofort zurückgegeben
    }
    public void maketoast(String content){
        Toast.makeText(getActivity(),content, Toast.LENGTH_SHORT).show();
    }
}