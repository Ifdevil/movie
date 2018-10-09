package com.stylefeng.guns.rest.modular.film.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.api.film.FilmServiceAPI;
import com.stylefeng.guns.api.film.vo.*;
import com.stylefeng.guns.core.util.DateUtil;
import com.stylefeng.guns.rest.common.persistence.dao.*;
import com.stylefeng.guns.rest.common.persistence.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Component
@Service(interfaceClass = FilmServiceAPI.class)
public class DefaultFilmServiceImpl implements FilmServiceAPI {

    @Autowired
    private MoocBannerTMapper moocBannerTMapper;

    @Autowired
    private MoocFilmTMapper moocFilmTMapper;

    @Autowired
    private MoocCatDictTMapper moocCatDictTMapper;

    @Autowired
    private MoocSourceDictTMapper moocSourceDictTMapper;

    @Autowired
    private MoocYearDictTMapper moocYearDictTMapper;

    @Override
    public List<BannerVO> getBanners() {
        List<BannerVO> result = new ArrayList<>();
        List<MoocBannerT> list = moocBannerTMapper.selectList(null);
        for(MoocBannerT moocBannerT:list){
            BannerVO bannerVO = new BannerVO();
            bannerVO.setBannerId(moocBannerT.getUuid()+"");
            bannerVO.setBannerAddress(moocBannerT.getBannerAddress());
            bannerVO.setBannerUrl(moocBannerT.getBannerUrl());
            result.add(bannerVO);
        }
        return result;
    }

    private List<FilmInfo> getFilmInfos(List<MoocFilmT> moocFilms){
        List<FilmInfo> result = new ArrayList<>();
        for (MoocFilmT moocFilmT:moocFilms){
            FilmInfo filmInfo = new FilmInfo();
            filmInfo.setScore(moocFilmT.getFilmScore());
            filmInfo.setImgAddress(moocFilmT.getImgAddress());
            filmInfo.setFilmType(moocFilmT.getFilmType());
            filmInfo.setFilmScore(moocFilmT.getFilmScore());
            filmInfo.setFilmName(moocFilmT.getFilmName());
            filmInfo.setFilmId(moocFilmT.getUuid()+"");
            filmInfo.setExpectNum(moocFilmT.getFilmPresalenum());
            filmInfo.setBoxNum(moocFilmT.getFilmBoxOffice());
            filmInfo.setShowTime(DateUtil.getDay(moocFilmT.getFilmTime()));
            //将转换的对象放入结果集
            result.add(filmInfo);
        }
        return result;
    }
    @Override
    public FilmVO getHotFilms(boolean isLimit, int nums,int nowPage,int sortId,int sourceId,int yearId,int catId) {
        FilmVO filmVO = new FilmVO();
        List<FilmInfo> result = new ArrayList<>();

        //热映影片限制条件
        EntityWrapper<MoocFilmT> wrapper = new EntityWrapper<>();
        wrapper.eq("film_status","1");
        //判断是否是首页显示的内容
        if(isLimit) {
            //如果是，则限制条数、限制内容为热映影片
            Page<MoocFilmT> page = new Page<>(1,nums);
            List<MoocFilmT> moocFilms = moocFilmTMapper.selectPage(page, wrapper);
            result = getFilmInfos(moocFilms);
            filmVO.setFilmNum(moocFilms.size());
            filmVO.setFilmInfo(result);
        }else {
            //如果不是，则是列表页，同样需要限制内容为热映影片
            Page<MoocFilmT> page = new Page<>(nowPage,nums);
            //如果sourceId,yearId,catId不为99，则表示按照对应的编号进行查询
            if(sourceId!=99){
                wrapper.eq("film_source",sourceId);
            }
            if(yearId!=99){
                wrapper.eq("film_date",yearId);
            }
            if(catId!=99){
                String catStr = "%#"+catId+"#%";
                wrapper.like("film_cats",catStr);
            }
            List<MoocFilmT> moocFilms = moocFilmTMapper.selectPage(page, wrapper);
            result = getFilmInfos(moocFilms);
            filmVO.setFilmNum(moocFilms.size());
            //需要的总页数 totalCounts/nums+1 ->
            int totalCounts = moocFilmTMapper.selectCount(wrapper);
            int totalPage  = (totalCounts/nums)+1;
            filmVO.setFilmInfo(result);
            filmVO.setTotalPage(totalCounts);
            filmVO.setNowPage(nowPage);
        }

        return filmVO;
    }

    @Override
    public FilmVO getSoonFilms(boolean isLimit, int nums,int nowPage,int sortId,int sourceId,int yearId,int catId) {
        FilmVO filmVO = new FilmVO();
        List<FilmInfo> result = new ArrayList<>();

        //即将上映影片限制条件
        EntityWrapper<MoocFilmT> wrapper = new EntityWrapper<>();
        wrapper.eq("film_status","2");
        //判断是否是首页显示的内容
        if(isLimit) {
            //如果是，则限制条数、限制内容为即将上映影片
            Page<MoocFilmT> page = new Page<>(1,nums);
            List<MoocFilmT> moocFilms = moocFilmTMapper.selectPage(page, wrapper);
            result = getFilmInfos(moocFilms);
            filmVO.setFilmNum(moocFilms.size());
            filmVO.setFilmInfo(result);
        }else {
            //如果不是，则是列表页，同样需要限制内容为即将上映影片
            Page<MoocFilmT> page = new Page<>(nowPage,nums);
            //如果sourceId,yearId,catId不为99，则表示按照对应的编号进行查询
            if(sourceId!=99){
                wrapper.eq("film_source",sourceId);
            }
            if(yearId!=99){
                wrapper.eq("film_date",yearId);
            }
            if(catId!=99){
                String catStr = "%#"+catId+"#%";
                wrapper.like("film_cats",catStr);
            }
            List<MoocFilmT> moocFilms = moocFilmTMapper.selectPage(page, wrapper);
            result = getFilmInfos(moocFilms);
            filmVO.setFilmNum(moocFilms.size());
            //需要的总页数 totalCounts/nums+1 ->
            int totalCounts = moocFilmTMapper.selectCount(wrapper);
            int totalPage  = (totalCounts/nums)+1;
            filmVO.setFilmInfo(result);
            filmVO.setTotalPage(totalCounts);
            filmVO.setNowPage(nowPage);
        }
        return filmVO;
    }

    @Override
    public FilmVO getClassicFilms(int nums, int nowPage, int sortId, int sourceId, int yearId, int catId) {
        FilmVO filmVO = new FilmVO();
        List<FilmInfo> result = new ArrayList<>();
        EntityWrapper<MoocFilmT> wrapper = new EntityWrapper<>();
        wrapper.eq("film_status","3");
        Page<MoocFilmT> page = new Page<>(nowPage,nums);
        //如果sourceId,yearId,catId不为99，则表示按照对应的编号进行查询
        if(sourceId!=99){
            wrapper.eq("film_source",sourceId);
        }
        if(yearId!=99){
            wrapper.eq("film_date",yearId);
        }
        if(catId!=99){
            String catStr = "%#"+catId+"#%";
            wrapper.like("film_cats",catStr);
        }
        List<MoocFilmT> moocFilms = moocFilmTMapper.selectPage(page, wrapper);
        result = getFilmInfos(moocFilms);
        filmVO.setFilmNum(moocFilms.size());
        //需要的总页数 totalCounts/nums+1 ->
        int totalCounts = moocFilmTMapper.selectCount(wrapper);
        int totalPage  = (totalCounts/nums)+1;
        filmVO.setFilmInfo(result);
        filmVO.setTotalPage(totalCounts);
        filmVO.setNowPage(nowPage);
        return filmVO;
    }

    @Override
    public List<FilmInfo> getBoxRanking() {
        //条件->正在上映的，票房前10名
        EntityWrapper<MoocFilmT> wrapper = new EntityWrapper<>();
        wrapper.eq("film_status","1");

        Page<MoocFilmT> page = new Page<>(1,10,"film_box_office");

        List<MoocFilmT> moocfilms = moocFilmTMapper.selectPage(page,wrapper);

        List<FilmInfo> filmInfos = getFilmInfos(moocfilms);
        return filmInfos;
    }

    @Override
    public List<FilmInfo> getExpectRanking() {
        //条件->即将上映的，预售前10名
        EntityWrapper<MoocFilmT> wrapper = new EntityWrapper<>();
        wrapper.eq("film_status","2");

        Page<MoocFilmT> page = new Page<>(1,10,"film_preSaleNum");

        List<MoocFilmT> moocfilms = moocFilmTMapper.selectPage(page,wrapper);

        List<FilmInfo> filmInfos = getFilmInfos(moocfilms);
        return filmInfos;
    }

    @Override
    public List<FilmInfo> getTop() {
        //条件->正在上映的，评分前10名
        EntityWrapper<MoocFilmT> wrapper = new EntityWrapper<>();
        wrapper.eq("film_status","1");

        Page<MoocFilmT> page = new Page<>(1,10,"film_score");

        List<MoocFilmT> moocfilms = moocFilmTMapper.selectPage(page,wrapper);

        List<FilmInfo> filmInfos = getFilmInfos(moocfilms);
        return filmInfos;
    }

    @Override
    public List<CatVO> getCats() {
        List<CatVO> cats = new ArrayList<>();
        //查询实体对象MoocCatDictT
        List<MoocCatDictT> moocCats = moocCatDictTMapper.selectList(null);
        //将实体对象转换为业务对象
        for (MoocCatDictT moocCatDictT:moocCats){
            CatVO catVO = new CatVO();
            catVO.setCatId(moocCatDictT.getUuid()+"");
            catVO.setCatName(moocCatDictT.getShowName());
            cats.add(catVO);
        }
        return cats;
    }

    @Override
    public List<SourceVO> getSources() {
        List<SourceVO> sources = new ArrayList<>();
        //查询实体对象MoocSourceDictT
        List<MoocSourceDictT> moocSources = moocSourceDictTMapper.selectList(null);
        //将实体对象转换为业务对象
        for (MoocSourceDictT moocSourceDictT:moocSources){
            SourceVO catVO = new SourceVO();
            catVO.setSourceId(moocSourceDictT.getUuid()+"");
            catVO.setSourceName(moocSourceDictT.getShowName());
            sources.add(catVO);
        }
        return sources;
    }

    @Override
    public List<YearVO> getYears() {
        List<YearVO> years = new ArrayList<>();
        //查询实体对象MoocYearDictT
        List<MoocYearDictT> moocyears = moocYearDictTMapper.selectList(null);
        //将实体对象转换为业务对象
        for (MoocYearDictT moocYearDictT:moocyears){
            YearVO yearVO = new YearVO();
            yearVO.setYearId(moocYearDictT.getUuid()+"");
            yearVO.setYearName(moocYearDictT.getShowName());
            years.add(yearVO);
        }
        return years;
    }
}
