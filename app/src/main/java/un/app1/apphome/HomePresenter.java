package un.app1.apphome;

import android.app.Activity;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;
import ss.com.bannerslider.banners.RemoteBanner;
import ss.com.bannerslider.views.BannerSlider;
import un.app1.R;
import un.app1.apphome.adapter.ProductModel;
import un.app1.apphome.model.ArrayBanner;
import un.app1.apphome.model.Banner;
import un.app1.apphome.model.MainMenuModel;
import un.app1.apphome.model.QuickPreview;
import un.app1.apphome.model.SubmitBanner;
import un.app1.apphome.model.SubmitQuickPreview;
import un.app1.network.service.MainService;
import un.app1.network.service.MyCallBack;

public class HomePresenter {

    private HomeView homeView;
    private MainService service;
    private CompositeSubscription subscriptions;
    private Context context;
    private int banner = 0;
    private int statusUserPreview = 0;
    private boolean islogin = false;

    HomePresenter(Activity activity, HomeView homeView, MainService service) {
        this.homeView = homeView;
        this.service = service;
        this.context = activity;
        subscriptions = new CompositeSubscription();
    }

    ArrayList<ProductModel> getProduct() {
        ArrayList<ProductModel> productModels = new ArrayList<>();
        productModels.add(new ProductModel(R.drawable.ic_menu_1, "xxxxxx"));
        productModels.add(new ProductModel(R.drawable.ic_menu_2, "xxxxxx"));
        productModels.add(new ProductModel(R.drawable.ic_menu_2, "xxxxxx"));
        return productModels;
    }

    ArrayList<MainMenuModel> getMainMenu() {
        ArrayList<MainMenuModel> mainMenuModel = new ArrayList<>();
        mainMenuModel.add(new MainMenuModel(context.getResources().getString(R.string.strTokenListrik)));
        mainMenuModel.add(new MainMenuModel(context.getResources().getString(R.string.strPulsa)));
        mainMenuModel.add(new MainMenuModel(context.getResources().getString(R.string.strVoucherGame)));
        mainMenuModel.add(new MainMenuModel(context.getResources().getString(R.string.strTiketKereta)));
        mainMenuModel.add(new MainMenuModel(context.getResources().getString(R.string.strHotel)));
        mainMenuModel.add(new MainMenuModel(context.getResources().getString(R.string.strTiketPesawat)));
        mainMenuModel.add(new MainMenuModel(context.getResources().getString(R.string.strBPJS)));
        mainMenuModel.add(new MainMenuModel(context.getResources().getString(R.string.strTopUpSaldo)));
        return mainMenuModel;
    }

    void checkInternet(boolean status) {
        if (!status) {
            homeView.animFadeOutArcLoader();
            homeView.snackBar();
        } else {
            if(islogin){
                if (banner == 0) {
                    homeView.animFadeInArcLoader();
                    homeView.ifBannerFailed();
                } if(statusUserPreview == 0){
                    homeView.ifUserPreviewFailed();
                }
            } else {
                if (banner == 0) {
                    homeView.animFadeInArcLoader();
                    homeView.ifBannerFailed();
                }
            }
        }
    }

    void isUserLogin(Activity activity){
        if(islogin){
            homeView.userPreview();
        } else {
            homeView.setLayoutSignOut();
        }
    }

    void getHomeBanner(SubmitBanner submitBanner) {
        homeView.animFadeInArcLoader();

        Subscription subscription = service.requestBanner(submitBanner, new MyCallBack.CallBanner() {

            @Override
            public void onError(String error) {
                homeView.animFadeOutArcLoader();
            }

            @Override
            public void onSuccess(Banner banner) {
                homeView.bannserSize(banner.arrayBanners);
                homeView.animFadeOutArcLoader();
                homeView.animFadeInBanner();
            }
        });

        subscriptions.add(subscription);
    }

    void getQuickPreview(SubmitQuickPreview submitQuickPreview) {
        homeView.setLayoutChecking();
        homeView.animFadeInChecking();
        Subscription subscription = service.requestQuickPreview(submitQuickPreview, new MyCallBack.CallQuickPreview() {

            @Override
            public void onError(String error) {
                homeView.animFadeOutChecking();
                homeView.animFadeInBlank();
                homeView.setLayoutBlank();
            }

            @Override
            public void onSuccess(QuickPreview quickPreview) {
                statusUserPreview = Integer.parseInt(quickPreview.statusCode);
                homeView.setLayoutSignIn();
                homeView.animFadeInSignIn();
                homeView.setUserName(quickPreview.user);
                homeView.setUserImage(quickPreview.imageUrl);
                homeView.setShowUserImage();
            }
        });

        subscriptions.add(subscription);
    }

    @SuppressWarnings("unused")
    void checkUserLogin() {
        homeView.setUserName("Login or Signup");
    }

    void setBanner(BannerSlider bannerSlider, List<ArrayBanner> arrayBanners) {
        banner = arrayBanners.size();
        for (int i = 0; i < arrayBanners.size(); i++) {
            bannerSlider.addBanner(new RemoteBanner(arrayBanners.get(i).url));
        }
    }

    public void onClickRetry() {
        homeView.onClickRetry();
    }

    public void onClickLogin() {
        homeView.onClickLogin();
    }

    public void onClickRegister() {
        homeView.onClickRegister();
    }

    void unSubscribe() {
        subscriptions.unsubscribe();
    }

}
