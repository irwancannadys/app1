package un.app1.apphome;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.view.Window;

import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import un.app1.MainApp;
import un.app1.R;
import un.app1.apphome.adapter.ProductAdapter;
import un.app1.apphome.model.SubmitBanner;
import un.app1.databinding.ActivityHomeBinding;
import un.app1.network.internet.ConnectivityReceiver;
import un.app1.network.service.MainService;
import un.app1.network.service.RetrofitBuilder;

public class ActivityHome extends AppCompatActivity implements HomeView, ConnectivityReceiver.ConnectivityReceiverListener {

    @Inject
    public RetrofitBuilder retrofitBuilder;

    @Inject
    public ProductAdapter productAdapter;

    ActivityHomeBinding binding;
    HomePresenter homePresenter;
    HomeViewModel homeViewModel;
    MainService mainService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setBinding();

        ((MainApp) getApplication()).providesAppComponents().inject(ActivityHome.this);
        MainApp.getInstance().setConnectivityListener(ActivityHome.this);

        setAdapter();

        homePresenter.getHomeBanner(new SubmitBanner("nbn b","n ,n ,"));
//        homePresenter.getPreviewUser();

    }

    private void setBinding() {
        binding = DataBindingUtil.setContentView(ActivityHome.this, R.layout.activity_home);
        mainService = new MainService(retrofitBuilder.service());
        homeViewModel = new HomeViewModel(new HomeMainModel());
        homePresenter = new HomePresenter(ActivityHome.this, mainService);
        binding.setViewModel(homeViewModel);
        binding.setPresenter(homePresenter);
    }

    private void setAdapter(){
        productAdapter.setViewData(ActivityHome.this);
        productAdapter.setProduct(homePresenter.getProduct());
        binding.recyclerProduct.setLayoutManager(new GridLayoutManager(ActivityHome.this, 2));
        binding.recyclerProduct.setAdapter(productAdapter);
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onDestroy(){
        homePresenter.unSubscribe();
        super.onDestroy();
    }

    @Override
    public void setShowUserImage(){
        Picasso.with(ActivityHome.this)
                .load(homeViewModel.getUserImage())
                .placeholder(R.drawable.ic_user)
                .resize(300, 300)
                .into(binding.imageUser);
    }

    @Override
    public void setUserImage(String imageUrl) {
        homeViewModel.setUserImage(imageUrl);
    }

    @Override
    public void goToActivity() {

    }

    @Override
    public void setUserName(String username) {
        homeViewModel.setUserName(username);
    }

    @Override
    public void setLogin(String login) {
        homeViewModel.setUserName(login);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }

    @Override
    public void showClickRetry() {
        binding.textRetry.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideClickRetry() {
        binding.textRetry.setVisibility(View.GONE);
    }

}