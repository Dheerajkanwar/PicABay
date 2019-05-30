package com.fitbell.dheerajkanwar.picabay.view;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;

import com.fitbell.dheerajkanwar.picabay.R;
import com.fitbell.dheerajkanwar.picabay.adapter.PictureListCardAdapter;
import com.fitbell.dheerajkanwar.picabay.databinding.PictureListActivityLayoutBinding;
import com.fitbell.dheerajkanwar.picabay.model.Hit;
import com.fitbell.dheerajkanwar.picabay.utils.CommonUtils;
import com.fitbell.dheerajkanwar.picabay.utils.RecyclerItemClickListener;
import com.fitbell.dheerajkanwar.picabay.viewmodel.PictureListViewModel;
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class PictureSearchListActivity extends AppCompatActivity {

    private PictureListActivityLayoutBinding mBinding ;
    private PictureListViewModel mViewModel ;
    private PictureListCardAdapter pictureCardAdapter;
    private RecyclerView recyclerPictureList;
    private GridLayoutManager layoutManager ;
    private ProgressBar progressBar ;

    private Disposable internetDisposable ;

    private boolean isLoadingPerformed = false ;
    private int lastVisibleItem, totalItemCount, appConnectivityStatus ;
    private final int VISIBLE_THRESHOLD = 1, APP_ONLINE = 1, APP_OFFLINE = 2 ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(PictureSearchListActivity.this, R.layout.picture_list_activity_layout);
        mViewModel = ViewModelProviders.of(this).get(PictureListViewModel.class);
        mViewModel.init();
        init();

    }

    private void init() {

        pictureCardAdapter = new PictureListCardAdapter(mViewModel.getPictures());
        layoutManager = new GridLayoutManager(this,2);

        progressBar = mBinding.progress ;
        recyclerPictureList = mBinding.recyclerNewsList ;

        //Setting toolbar
        setSupportActionBar(mBinding.tbNewsList);


        //Setting recycler
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        layoutManager.setSmoothScrollbarEnabled(true);
        recyclerPictureList.setLayoutManager(layoutManager);
        recyclerPictureList.setAdapter(pictureCardAdapter);
        setUpLoadMoreListener();
        addOnItemClickListener();

        //To prevent Api calling on orientation change
        if(mViewModel.isOnCreateCalledOnce()){
            if(CommonUtils.isInternetAvailable(PictureSearchListActivity.this)) appConnectivityStatus = 1 ;
            else appConnectivityStatus = 2 ;
            loadData();
        }

        mViewModel.getIsDataLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                handleProgressBar(aBoolean);
            }
        });

        mViewModel.getMessageInfo().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                showSnackBar(s);
            }
        });

        mViewModel.getLatestImages().observe(this, new Observer<ArrayList<Hit>>() {
            @Override
            public void onChanged(ArrayList<Hit> articles) {
                isLoadingPerformed = false ;
                inflateLatestNews();
            }
        });

        mViewModel.getAppConnectivityStatus().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                appConnectivityStatus = integer ;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Checks for Internet connectivity
        internetDisposable = ReactiveNetwork.observeInternetConnectivity()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        //To check if ONLINE or OFFLINE, snackbar shows up only on connectivity changes
                        if ((appConnectivityStatus == 1 && !aBoolean) || (appConnectivityStatus == 2 && aBoolean)) mViewModel.onAppConnectivityChange(aBoolean);
                    }
                });
    }


    @Override
    protected void onPause() {
        super.onPause();
        safelyDispose(internetDisposable);
    }

    //To safely dispose observable
    private void safelyDispose(Disposable disposable) {
        if(disposable != null && !disposable.isDisposed()) disposable.dispose();
    }


    //To set up custom scroll end listener on recycler
    private void setUpLoadMoreListener() {
        recyclerPictureList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView,
                                   int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = layoutManager.getItemCount();
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                if (!isLoadingPerformed && totalItemCount <= (lastVisibleItem + VISIBLE_THRESHOLD)) {
                    isLoadingPerformed = true;
                    loadData();
                }
            }
        });
    }

    //To set up custom click listener
    private void addOnItemClickListener() {
        recyclerPictureList.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerPictureList, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
              /*  ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(PictureSearchListActivity.this, view.findViewById(R.id.img_article), "news_pic");
                Intent intent = new Intent(PictureSearchListActivity.this, NewsDetailActivity.class);
                intent.putExtra("article", mViewModel.getArticleWithPosition(position));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) startActivity(intent, options.toBundle());
                else startActivity(intent);*/
            }
        }));
    }

    //To inflate adapter from news articles in Model class
    private void inflateLatestNews() {
        pictureCardAdapter.addDatatoAdapter();
    }


    //To provide visual clues regarding ERROR, SUCCESS and ACTION
    private void showSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(mBinding.lytCoordinatorNewsList, message, Snackbar.LENGTH_INDEFINITE);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.resetVals();
                loadData();
            }
        };

        switch (appConnectivityStatus) {
            case APP_ONLINE : snackbar.setAction(R.string.btn_go_online,listener);
                break;

            case APP_OFFLINE : snackbar.setAction(R.string.btn_go_offline,listener);
                break;

        }
        snackbar.show();
    }


    //To handle progress bar visibility
    private void handleProgressBar(boolean visibility) {
        if(visibility) progressBar.setVisibility(View.VISIBLE);
        else progressBar.setVisibility(View.GONE);
    }


    //To load data from Server or DB
    private void loadData() {
        if(appConnectivityStatus == 1) mViewModel.loadLatestImagesFromApi();
       // else mViewModel.loadNewsFromDB();
    }

}
