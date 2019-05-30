package com.fitbell.dheerajkanwar.picabay.viewmodel;

import com.fitbell.dheerajkanwar.picabay.model.Hit;
import com.fitbell.dheerajkanwar.picabay.model.PictureListMain;
import com.fitbell.dheerajkanwar.picabay.repository.PictureListRepository;
import com.fitbell.dheerajkanwar.picabay.utils.AppConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class PictureListViewModel extends ViewModel {

    private MutableLiveData<ArrayList<Hit>> mPictureListLiveData = new MutableLiveData<>() ;
    private MutableLiveData<Boolean> mIsDataLoading = new MutableLiveData<>() ;
    private MutableLiveData<String> mMessageInfo = new MutableLiveData<>() ;
    private MutableLiveData<Integer> mAppConnectivityStatus = new MutableLiveData<>() ;

    private PictureListRepository mRepo ;

    private DisposableObserver<PictureListMain> disposableObserver ;
    //private DisposableObserver<List<Hit>> dbDisposableObserver ;

    private ArrayList<Hit> pictures = null ;
    private boolean isOnCreateCalledOnce = true ;
    private int totalPage, currentPage ;

    public void init() {
        if(isOnCreateCalledOnce) {
            this.mRepo = PictureListRepository.getInstance();
            this.totalPage = 1 ;
            this.currentPage = 1 ;
            if(pictures == null) pictures = new ArrayList<>();
        }
    }

    public LiveData<Integer> getAppConnectivityStatus() { return mAppConnectivityStatus; }

    public LiveData<String> getMessageInfo() { return mMessageInfo; }

    public LiveData<Boolean> getIsDataLoading() { return mIsDataLoading; }

    public LiveData<ArrayList<Hit>> getLatestImages() {
        return mPictureListLiveData;
    }


    //To post internet connectivity back to activity 1 for ONLINE, 2 for OFFLINE
    public void onAppConnectivityChange(boolean status) {
        if (status) {
            mAppConnectivityStatus.postValue(1);
            mMessageInfo.postValue("Internet available");
        } else {
            mAppConnectivityStatus.postValue(2);
            mMessageInfo.postValue("Internet disconnected");
        }
    }

    //To get already loaded list of news pictures
    public ArrayList<Hit> getPictures() {
        return this.pictures;
    }

    //To get article on card click
    public Hit getArticleWithPosition(int position) { return this.pictures.get(position); }

    //To check if onCreate was called or not
    public boolean isOnCreateCalledOnce() {
        return isOnCreateCalledOnce;
    }


    //To switch app between Offline and Online Modes
    public void resetVals() {
        this.totalPage = 1 ;
        this.currentPage = 1 ;
        this.pictures.clear();
    }


    //To load news from Server
    public void loadLatestImagesFromApi() {

        this.isOnCreateCalledOnce = false ;

        if(currentPage <= totalPage) {

            mIsDataLoading.postValue(true);
            disposableObserver = new DisposableObserver<PictureListMain>() {

                @Override
                public void onNext(PictureListMain pictureListMain) {
                    currentPage += 1 ;
                    mIsDataLoading.postValue(false);
                    onNewsDataReceivedFromApi(pictureListMain);
                }

                @Override
                public void onError(Throwable e) {
                }

                @Override
                public void onComplete() {
                }
            };

            mRepo.getNewsFromApi(this.currentPage)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .debounce(400, TimeUnit.MILLISECONDS)
                    .subscribe(disposableObserver);

        }

    }

    //To handle news received from Server
    private void onNewsDataReceivedFromApi(PictureListMain pictureListMain) {

        if(pictureListMain != null) {

            int totalResultsFound = pictureListMain.getTotal() ;
            if(totalResultsFound != 0){

                //Update the page size and update the list
                this.totalPage = ( totalResultsFound / AppConstants.PAGE_SIZE ) + 1 ;
                this.pictures.addAll(pictureListMain.getHits());

            }else {

                //Update the list
                this.pictures.clear();
                mMessageInfo.postValue("No images found !");

            }

            mPictureListLiveData.postValue(pictures);

        }else mMessageInfo.postValue("Oops ! Problem loading images");

    }



    //Load data from Database
   /* public void loadNewsFromDB() {

        this.isOnCreateCalledOnce = false ;

        mIsDataLoading.postValue(true);
        dbDisposableObserver = new DisposableObserver<List<Hit>>() {

            @Override
            public void onNext(List<Hit> articles) {
                currentPage += 1 ;
                mIsDataLoading.postValue(false);
                onNewsReceivedFromDb(articles);
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        };

        mRepo.getNewsFromDB(AppConstants.PAGE_SIZE, AppConstants.PAGE_SIZE * this.currentPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(dbDisposableObserver);

    }*/

    //To handle news received from Database
    private void onNewsReceivedFromDb(List<Hit> articles) {

        if(articles.size() != 0) this.pictures.addAll(articles);
        else {
            this.pictures.clear();
            mMessageInfo.postValue("No news stored !");
        }

        mPictureListLiveData.postValue((ArrayList<Hit>) articles);

    }


    @Override
    protected void onCleared() {
        super.onCleared();
        if(null != disposableObserver && !disposableObserver.isDisposed()) disposableObserver.dispose() ;
       // if(null != dbDisposableObserver && !dbDisposableObserver.isDisposed()) dbDisposableObserver.dispose() ;
    }


}
