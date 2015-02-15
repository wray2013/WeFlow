package com.cmmobi.railwifi.sql;

import com.cmmobi.railwifi.dao.Fav;
import com.cmmobi.railwifi.dao.PlayHistory;
import com.cmmobi.railwifi.network.GsonResponseObject;

public class SqlConvertor {
	public static PlayHistory sohuMedia2PlayHistory(
			GsonResponseObject.mediaElem mItem, String url, int media_type, String location, double precent) {
		PlayHistory r = new PlayHistory();
		if (media_type == GsonResponseObject.MEDIA_TYPE_MOVIE) {
			r.setImg_path(mItem.img_path);
			r.setLocation(location);
			r.setPercent(String.valueOf(precent));
			r.setMedia_id(mItem.media_id);
			r.setMedia_type(media_type);
			r.setName(mItem.name);
			r.setSrc_url(url);
		}

		return r;

	}
	
	public static PlayHistory mediaDetail2PlayHistory(
			GsonResponseObject.mediaDetailInfoResp mItem, int media_type, String location, double precent) {
		PlayHistory r = new PlayHistory();
		if (media_type == GsonResponseObject.MEDIA_TYPE_MOVIE) {
			r.setImg_path(mItem.img_path);
			r.setLocation(location);
			r.setPercent(String.valueOf(precent));
			r.setMedia_id(mItem.media_id);
			r.setMedia_type(media_type);
			r.setName(mItem.name);
			r.setSrc_url(mItem.src_path);
		}

		return r;

	}
	
	public static PlayHistory recommendDetail2PlayHistory(GsonResponseObject.recommandDetail mItem,int media_type) {
		PlayHistory r = new PlayHistory();
		if (media_type == GsonResponseObject.MEDIA_TYPE_MOVIE) {
			r.setImg_path(mItem.img_path);
			r.setLocation("");
			r.setPercent(String.valueOf(0));
			r.setMedia_id(mItem.media_id);
			r.setMedia_type(media_type);
			r.setName(mItem.name);
			r.setSrc_url(mItem.src_path);
		}
		return r;
	}
	
	public static PlayHistory mediaItem2PlayHistory(
			GsonResponseObject.mediaElem mItem, int media_type, String location, double precent) {
		PlayHistory r = new PlayHistory();
		if (media_type == GsonResponseObject.MEDIA_TYPE_MOVIE) {
			r.setImg_path(mItem.img_path);
			r.setLocation(location);
			r.setPercent(String.valueOf(precent));
			r.setMedia_id(mItem.media_id);
			r.setMedia_type(media_type);
			r.setName(mItem.name);
			r.setSrc_url(mItem.src_path);
		}

		return r;

	}
	
	public static PlayHistory mediaFav2PlayHistory(
			Fav mItem, int media_type, String location, double precent) {
		PlayHistory r = new PlayHistory();
		if (media_type == GsonResponseObject.MEDIA_TYPE_MOVIE) {
			r.setImg_path(mItem.getImg_path());
			r.setLocation(location);
			r.setPercent(String.valueOf(precent));
			r.setMedia_id(mItem.getMedia_id());
			r.setMedia_type(media_type);
			r.setName(mItem.getName());
			r.setSrc_url(mItem.getSrc_path());
		}

		return r;

	}
	
	public static Fav mediaDetail2Fav(
			GsonResponseObject.mediaDetailInfoResp mItem, int media_type) {
		Fav r = new Fav();
		if (media_type == GsonResponseObject.MEDIA_TYPE_MOVIE) {
			r.setActors(mItem.actors);
			r.setColor(mItem.color);
			r.setDirector(mItem.director);
			r.setImg_path(mItem.img_path);
			r.setIntroduction(mItem.introduction);
			r.setMedia_id(mItem.media_id);
			r.setMedia_type(media_type);
			r.setName(mItem.name);
			r.setScore(mItem.score);
			r.setSource(mItem.source);
			r.setSource_id(mItem.source_id);
			r.setSrc_path(mItem.src_path);
			r.setTag(mItem.tag);
		}

		return r;

	}

	public static Fav mediaItem2Fav(GsonResponseObject.mediaElem mItem,
			int media_type) {
		Fav r = new Fav();
		if (media_type == GsonResponseObject.MEDIA_TYPE_MOVIE) {
			r.setActors(mItem.actors);
			r.setColor(mItem.color);
			r.setDirector(mItem.director);
			r.setImg_path(mItem.img_path);
			r.setIntroduction(mItem.introduction);
			r.setMedia_id(mItem.media_id);
			r.setMedia_type(media_type);
			r.setName(mItem.name);
			r.setScore(mItem.score);
			r.setSource(mItem.source);
			r.setSource_id(mItem.source_id);
			r.setSrc_path(mItem.src_path);
			r.setTag(mItem.tag);
		}

		return r;

	}

}
