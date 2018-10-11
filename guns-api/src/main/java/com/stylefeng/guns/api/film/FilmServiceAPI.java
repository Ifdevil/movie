package com.stylefeng.guns.api.film;

import com.stylefeng.guns.api.film.vo.*;

import java.util.List;

public interface FilmServiceAPI {

    //获取banners
    List<BannerVO> getBanners();
    //获取热映影片
    FilmVO getHotFilms(boolean isLimit,int nums,int nowPage,int sortId,int sourceId,int yearId,int catId);
    /*
        项目中推荐大家使用-//获取热映影片列表
        FilmVO getHotFilms(int nowpage,int nums ...);
     */

    //获取即将上映的影片【受欢迎程度排序】
    FilmVO getSoonFilms(boolean isLimit,int nums,int nowPage,int sortId,int sourceId,int yearId,int catId);
    //获取经典影片
    FilmVO getClassicFilms(int nums,int nowPage,int sortId,int sourceId,int yearId,int catId);
    //获取票房排行榜
    List<FilmInfo> getBoxRanking();
    //获取人气排行榜
    List<FilmInfo> getExpectRanking();
    //获取Top100
    List<FilmInfo> getTop();

    //---获取影片条件接口
    //分类条件
    List<CatVO> getCats();
    //片源条件
    List<SourceVO> getSources();
    //年代条件
    List<YearVO> getYears();

    //根据影片Id和影片名称获取影片信息
    FilmDetailVO getFilmDetail(int searchType,String searchParam);
    //获取影片相关的其他信息【演员表、图片地址...】
    //获取影片描述信息
    FilmDescVO getFilmDesc(String filmId);
    //获取图片信息
    ImgVO getImgs(String filmId);
    //获取导演信息
    ActorVO getDescInfo(String filmId);
    //获取演员信息
    List<ActorVO> getActors(String filmId);
}
