package com.stylefeng.guns.rest.modular.film;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.rpc.RpcContext;
import com.stylefeng.guns.api.film.FilmAsyncServiceAPI;
import com.stylefeng.guns.api.film.FilmServiceAPI;
import com.stylefeng.guns.api.film.vo.*;
import com.stylefeng.guns.api.user.UserAPI;
import com.stylefeng.guns.rest.modular.film.vo.FilmConditionVO;
import com.stylefeng.guns.rest.modular.film.vo.FilmIndexVO;
import com.stylefeng.guns.rest.modular.film.vo.FilmRequestVO;
import com.stylefeng.guns.rest.modular.vo.ResponseVO;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RestController
@RequestMapping("/film/")
public class FilmController {

    private static final String IMG_PRE="http://img.meetingshop.cn/";

    @Reference(interfaceClass = FilmServiceAPI.class,check = false)
    private FilmServiceAPI filmServiceAPI;

    @Reference(interfaceClass = FilmAsyncServiceAPI.class,check = false,async = true)
    private FilmAsyncServiceAPI filmAsyncServiceAPI;

    /**
     * 获取首页信息接口
     * API网关：
     *          1、功能聚合【API聚合】
     *          好处：1、六个接口 一次请求 同一时刻节省了5次Http请求
     *                2、同一个接口对外暴露、降低前后端分离开发的难度和复杂度
     *          坏处：1、一次数据过多，容易出现问题
     * @return
     */
    @RequestMapping(value = "getIndex",method = RequestMethod.GET)
    public ResponseVO getIndex(){
        FilmIndexVO filmIndexVO = new FilmIndexVO();
        //获取banner信息
        filmIndexVO.setBanners(filmServiceAPI.getBanners());
        //获取热映的电影
        filmIndexVO.setHotFilms(filmServiceAPI.getHotFilms(true,8,1,99,99,99,99));
        //获取即将上映的电影
        filmIndexVO.setSoonFilms(filmServiceAPI.getSoonFilms(true,8,1,99,99,99,99));
        //票房排行榜
        filmIndexVO.setBoxRanking(filmServiceAPI.getBoxRanking());
        //获取受欢迎
        filmIndexVO.setExpectRanking(filmServiceAPI.getExpectRanking());
        //榜单前一百
        filmIndexVO.setTop100(filmServiceAPI.getTop());

        return ResponseVO.success(IMG_PRE,filmIndexVO);
    }


    @RequestMapping(value = "getConditionList",method = RequestMethod.GET)
    public ResponseVO getConditionList(@RequestParam(name = "catId",required = false,defaultValue = "99")String catId,
                                       @RequestParam(name = "sourceId",required = false,defaultValue = "99")String sourseId,
                                       @RequestParam(name = "yearId",required = false,defaultValue = "99")String yearId){

        FilmConditionVO filmConditionVO = new FilmConditionVO();
        //标识位
        boolean flag = false;
        //类型集合
        List<CatVO> cats = filmServiceAPI.getCats();
        List<CatVO> catResult = new ArrayList<>();
        CatVO cat = null;
        for(CatVO catVO:cats){
            // 判断集合是否存在catId，如果存在，则将对应的实体变成active状态
            //输入6
            //1,2,3,99,4,5 ->
            /*
                优化：【理论上】
                    1.数据层查询按Id进行排序【有序集合 -> 有序数组】
                    2.通过二分法查找
             */
            if(catVO.getCatId().equals("99")){
                cat = catVO;
                continue;
            }
            if(catVO.getCatId().equals(catId)){
                flag = true;
                catVO.setActive(true);
            }else{
                catVO.setActive(false);
            }
            catResult.add(catVO);
        }
        // 如果不存在，则默认将全部变为Active状态
        if(!flag){
            cat.setActive(true);
            catResult.add(cat);
        }else{
            cat.setActive(false);
            catResult.add(cat);
        }
         //片源集合
        flag=false;
        List<SourceVO> sources = filmServiceAPI.getSources();
        List<SourceVO> sourceResult = new ArrayList<>();
        SourceVO sourceVO = null;
        for(SourceVO source : sources){
            if(source.getSourceId().equals("99")){
                sourceVO = source;
                continue;
            }
            if(source.getSourceId().equals(catId)){
                flag = true;
                source.setActive(true);
            }else{
                source.setActive(false);
            }
            sourceResult.add(source);
        }
        // 如果不存在，则默认将全部变为Active状态
        if(!flag){
            sourceVO.setActive(true);
            sourceResult.add(sourceVO);
        }else{
            sourceVO.setActive(false);
            sourceResult.add(sourceVO);
        }

        //年代集合
        flag=false;
        List<YearVO> years = filmServiceAPI.getYears();
        List<YearVO> yearResult = new ArrayList<>();
        YearVO yearVO = null;
        for(YearVO year : years){
            if(year.getYearId().equals("99")){
                yearVO = year;
                continue;
            }
            if(year.getYearId().equals(catId)){
                flag = true;
                year.setActive(true);
            }else{
                year.setActive(false);
            }
            yearResult.add(year);
        }
        // 如果不存在，则默认将全部变为Active状态
        if(!flag){
            yearVO.setActive(true);
            yearResult.add(yearVO);
        }else{
            yearVO.setActive(false);
            yearResult.add(yearVO);
        }

        filmConditionVO.setCatInfo(catResult);
        filmConditionVO.setSourceInfo(sourceResult);
        filmConditionVO.setYearInfo(yearResult);

        return ResponseVO.success(filmConditionVO);
    }


    @RequestMapping(value = "getFilms",method = RequestMethod.GET)
    public ResponseVO getFilms(FilmRequestVO filmRequestVO){
        String img_pre = "http://img.meetingshop.cn";
        FilmVO filmVO = null;
        //根据showType判断影片查询类型
        //根据sortId排序
        //根据各种条件查询
        //判断当前页是第几页
        switch (filmRequestVO.getShowType()){
            case 1:
                filmVO = filmServiceAPI.getHotFilms(false,filmRequestVO.getPageSize(),filmRequestVO.getNowPage(),
                                                       filmRequestVO.getSortId(),filmRequestVO.getSourceId(),filmRequestVO.getYearId(),
                                                       filmRequestVO.getCatId());
                break;
            case 2:
                filmVO = filmServiceAPI.getSoonFilms(false,filmRequestVO.getPageSize(),filmRequestVO.getNowPage(),
                        filmRequestVO.getSortId(),filmRequestVO.getSourceId(),filmRequestVO.getYearId(),
                        filmRequestVO.getCatId());
                break;
            case 3:
                filmVO = filmServiceAPI.getClassicFilms(filmRequestVO.getPageSize(),filmRequestVO.getNowPage(),
                        filmRequestVO.getSortId(),filmRequestVO.getSourceId(),filmRequestVO.getYearId(),
                        filmRequestVO.getCatId());
                break;
            default:
                filmVO = filmServiceAPI.getHotFilms(false,filmRequestVO.getPageSize(),filmRequestVO.getNowPage(),
                        filmRequestVO.getSortId(),filmRequestVO.getSourceId(),filmRequestVO.getYearId(),
                        filmRequestVO.getCatId());
                break;
        }


        return ResponseVO.success(filmRequestVO.getNowPage(),filmRequestVO.getPageSize(),img_pre,filmVO.getFilmInfo());
    }

    @RequestMapping(value = "films/{searchParam}",method = RequestMethod.GET)
    public ResponseVO films(@PathVariable("searchParam")String searchParam,int searchType) throws ExecutionException, InterruptedException {
        String img_pre = "http://img.meetingshop.cn";
        // 根据searchType，判断查询类型
        FilmDetailVO filmDetail = filmServiceAPI.getFilmDetail(searchType, searchParam);
        if(filmDetail==null){
            return ResponseVO.serviceFail("没有可查询的影片");
        }else if(filmDetail.getFilmId()==null || filmDetail.getFilmId().trim().length()==0){
            return ResponseVO.serviceFail("没有可查询的影片");
        }
        String filmId = filmDetail.getFilmId();
        //获取影片详细信息 ->Dubbo异步调用
        //获取影片描述信息
        //FilmDescVO filmDescVO = filmAsyncServiceAPI.getFilmDesc(filmId);
        filmAsyncServiceAPI.getFilmDesc(filmId);
        Future<FilmDescVO> filmDescVOFuture = RpcContext.getContext().getFuture();
        //获取图片信息
        //ImgVO imgVO = filmAsyncServiceAPI.getImgs(filmId);
        filmAsyncServiceAPI.getImgs(filmId);
        Future<ImgVO> imgVOFuture = RpcContext.getContext().getFuture();
        //获取导演信息
        //ActorVO directorVO = filmAsyncServiceAPI.getDescInfo(filmId);
        filmAsyncServiceAPI.getDescInfo(filmId);
        Future<ActorVO> actorVOFuture = RpcContext.getContext().getFuture();
        //获取演员信息
        //List<ActorVO> actors = filmAsyncServiceAPI.getActors(filmId);
        filmAsyncServiceAPI.getActors(filmId);
        Future<List<ActorVO>> listFuture = RpcContext.getContext().getFuture();

        //组织info对象
        InfoRequstVO infoRequstVO = new InfoRequstVO();
        //组织actor属性
        ActorRequestVO actorRequestVO = new ActorRequestVO();
        actorRequestVO.setDirector(actorVOFuture.get());
        actorRequestVO.setActors(listFuture.get());

        infoRequstVO.setActors(actorRequestVO);
        infoRequstVO.setBiography(filmDescVOFuture.get().getBiography());
        infoRequstVO.setImgVO(imgVOFuture.get());
        infoRequstVO.setFilmId(filmId);

        filmDetail.setInfo04(infoRequstVO);
        return ResponseVO.success(img_pre,filmDetail);

    }
}
