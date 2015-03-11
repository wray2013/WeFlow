package net.etoc.core.service.impl;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import com.jhlabs.image.PinchFilter;
import com.jhlabs.image.WaterFilter;
import com.jhlabs.math.ImageFunction2D;
import com.octo.captcha.component.image.backgroundgenerator.BackgroundGenerator;
import com.octo.captcha.component.image.backgroundgenerator.UniColorBackgroundGenerator;
import com.octo.captcha.component.image.color.RandomListColorGenerator;
import com.octo.captcha.component.image.deformation.ImageDeformation;
import com.octo.captcha.component.image.deformation.ImageDeformationByBufferedImageOp;
import com.octo.captcha.component.image.fontgenerator.FontGenerator;
import com.octo.captcha.component.image.fontgenerator.RandomFontGenerator;
import com.octo.captcha.component.image.textpaster.GlyphsPaster;
import com.octo.captcha.component.image.textpaster.TextPaster;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.GlyphsVisitors;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.OverlapGlyphsUsingShapeVisitor;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.TranslateAllToRandomPointVisitor;
import com.octo.captcha.component.image.textpaster.glyphsvisitor.TranslateGlyphsVerticalRandomVisitor;
import com.octo.captcha.component.image.wordtoimage.DeformedComposedWordToImage;
import com.octo.captcha.component.image.wordtoimage.WordToImage;
import com.octo.captcha.component.word.wordgenerator.RandomWordGenerator;
import com.octo.captcha.component.word.wordgenerator.WordGenerator;
import com.octo.captcha.engine.image.ListImageCaptchaEngine;
import com.octo.captcha.image.gimpy.GimpyFactory;

public class AppCaptchaEngine extends ListImageCaptchaEngine {

	protected void buildInitialFactories() {

		WordGenerator dictionnaryWords = new RandomWordGenerator(
				"0123456789abcdefghijklmnopqrstuvwxyz");
		// WordGenerator dictionnaryWords = new ComposeDictionaryWordGenerator(
		// new FileDictionary("toddlist"));

		// 文字干扰器--- 可以创建多个
		TextPaster randomPaster = new GlyphsPaster(4, 4,
				new RandomListColorGenerator(new Color[] {
						new Color(23, 170, 27), new Color(220, 34, 11),
						new Color(23, 67, 172) }), new GlyphsVisitors[] {
						new TranslateGlyphsVerticalRandomVisitor(1),
						new OverlapGlyphsUsingShapeVisitor(3),
						new TranslateAllToRandomPointVisitor() });
		BackgroundGenerator back = new UniColorBackgroundGenerator(80, 30,
				Color.white);

		FontGenerator shearedFont = new RandomFontGenerator(30, 30, new Font[] {
				new Font("nyala", Font.BOLD, 30),
				new Font("Bell MT", Font.PLAIN, 30),
				new Font("Credit valley", Font.BOLD, 30) }, false);

		PinchFilter pinch = new PinchFilter();// 扭曲干扰

		pinch.setAmount(-.1f);
		pinch.setRadius(10);
		pinch.setAngle((float) (Math.PI / 16));
		pinch.setCentreX(0.1f);
		pinch.setCentreY(-0.01f);
		pinch.setEdgeAction(ImageFunction2D.WRAP);

		// 过滤器
		WaterFilter water = new WaterFilter();
		water.setAmplitude(1f);// 振幅
		// water.setPhase(30f);// 月亮的盈亏
		water.setWavelength(1f);
		List<ImageDeformation> backDef = new ArrayList<ImageDeformation>();
		List<ImageDeformation> textDef = new ArrayList<ImageDeformation>();
		List<ImageDeformation> postDef = new ArrayList<ImageDeformation>();
		// textDef.add(new ImageDeformationByBufferedImageOp(water));
		textDef.add(new ImageDeformationByBufferedImageOp(pinch));
		WordToImage word2image = new DeformedComposedWordToImage(false,
				shearedFont, back, randomPaster, backDef, textDef, postDef

		);

		this.addFactory(new GimpyFactory(dictionnaryWords, word2image, false));

	}
}