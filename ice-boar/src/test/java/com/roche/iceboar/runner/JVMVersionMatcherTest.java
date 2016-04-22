package com.roche.iceboar.runner;

import com.roche.iceboar.settings.GlobalSettings;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/*  spec

1.4.2 - run only on 1.4.2
1.4.2_04 - run only on 1.4.2_04
1.4+ - run on all 1.4 versions (prefix match)
1.4 - run on all 1.4 versions (prefix match)
1.5 1.6 - run on all 1.5 or all 1.6
1.5, 1.6 - run on all 1.5 or all 1.6
1.5+ 1.6+ - run on all 1.5 or all 1.6
1.5+, 1.6+ - run on all 1.5 or all 1.6
1.6 1.7.0_01 - run on all 1.6 or only 1.7.0_01


 */

public class JVMVersionMatcherTest {

	public static final String JAVA_14 = "1.4";
	public static final String JAVA_14_MIN = "1.4+";
	public static final String JAVA_142 = "1.4.2";
	public static final String JAVA_142_04 = "1.4.2_04";

	public static final String JAVA_15 = "1.5";
	public static final String JAVA_15_MIN = "1.5+";
	public static final String JAVA_150 = "1.5.0";
	public static final String JAVA_150_17 = "1.5.0_17";

	public static final String JAVA_16 = "1.6";

	private JVMVersionMatcher sut;

	@BeforeMethod
	public void setUp() throws Exception {
		sut = new JVMVersionMatcher();
	}

	@Test
	public void shouldMatchSimpleFullVersion() {
		// given
		GlobalSettings settingsThatMatch = getBuild(JAVA_142, JAVA_142);
		GlobalSettings settingsThatNotMatch = getBuild(JAVA_150, JAVA_142);
		// then
		assertThat(sut.match(settingsThatMatch)).isTrue();
		assertThat(sut.match(settingsThatNotMatch)).isFalse();
	}

	@Test
	public void shouldMatchIfCurrentReleaseIsMoreStrictThanTarget() {
		// given
		GlobalSettings settingsThatMatch = getBuild(JAVA_142_04, JAVA_142);
		assertThat(sut.match(settingsThatMatch)).isTrue();
	}

	@Test
	public void shouldNotMatchIfTargetIsMoreStrictThanCurrentRelease() {
		// given
		GlobalSettings settingsThatMatch = getBuild(JAVA_142, JAVA_142_04);
		assertThat(sut.match(settingsThatMatch)).isFalse();
	}

	@Test
	public void shouldMatchIfCurrentMinorIsMoreStrictThanTarget() {
		// given
		GlobalSettings settingsThatMatch = getBuild(JAVA_142, JAVA_14);
		assertThat(sut.match(settingsThatMatch)).isTrue();
	}


	@Test
	public void shouldNotMatchIfTargetIsMoreStrictThanCurrentMinor() {
		// given
		GlobalSettings settingsThatMatch = getBuild(JAVA_14, JAVA_142);
		assertThat(sut.match(settingsThatMatch)).isFalse();
	}

	@Test
	public void shouldMatchIfTargetMinimumIsEqualToCurrent() {
		// given
		GlobalSettings settingsThatMatchMinor = getBuild(JAVA_14, JAVA_14_MIN);
		GlobalSettings settingsThatMatchMicro = getBuild(JAVA_142, JAVA_14_MIN);
		GlobalSettings settingsThatMatchRelease = getBuild(JAVA_142_04, JAVA_14_MIN);

		assertAllTrue(settingsThatMatchMinor, settingsThatMatchMicro, settingsThatMatchRelease);
	}

	@Test
	public void shouldMatchIfCurrentMinorBetterThanTargetMinimum() {
		GlobalSettings settingsThatMatchMinor = getBuild(JAVA_15, JAVA_14_MIN);
		GlobalSettings settingsThatMatchMicro = getBuild(JAVA_150, JAVA_14_MIN);
		GlobalSettings settingsThatMatchRelease = getBuild(JAVA_150_17, JAVA_14_MIN);

		assertAllTrue(settingsThatMatchMinor, settingsThatMatchMicro, settingsThatMatchRelease);
	}

	@Test
	public void shouldNotMatchIfCurrentMinorLessThanTargetMinimum() {
		GlobalSettings settingsThatMatchMinor = getBuild(JAVA_14, JAVA_15_MIN);
		GlobalSettings settingsThatMatchMicro = getBuild(JAVA_142, JAVA_15_MIN);
		GlobalSettings settingsThatMatchRelease = getBuild(JAVA_142_04, JAVA_15_MIN);

		assertAllFalse(settingsThatMatchMinor, settingsThatMatchMicro, settingsThatMatchRelease);
	}

	@Test
	public void shouldMatchIfCurrentMatchToAnyInTargets() {
		// given
		GlobalSettings settingsThatMatchByComa = getBuild(JAVA_142, JAVA_150 + ", " + JAVA_142);
		GlobalSettings settingsThatMatchBySpace = getBuild(JAVA_142, JAVA_150 + " " + JAVA_142);
		GlobalSettings settingsThatNotMatchByComa = getBuild(JAVA_142, JAVA_150 + ", " + JAVA_16);
		GlobalSettings settingsThatNotMatchBySpace = getBuild(JAVA_142, JAVA_150 + " " + JAVA_16);

		assertThat(sut.match(settingsThatMatchByComa)).isTrue();
		assertThat(sut.match(settingsThatMatchBySpace)).isTrue();
		assertThat(sut.match(settingsThatNotMatchByComa)).isFalse();
		assertThat(sut.match(settingsThatNotMatchBySpace)).isFalse();
	}

	private void assertAllFalse(GlobalSettings settingsThatMatchMinor, GlobalSettings settingsThatMatchMicro, GlobalSettings settingsThatMatchRelease) {
		assertThat(sut.match(settingsThatMatchMinor)).isFalse();
		assertThat(sut.match(settingsThatMatchMicro)).isFalse();
		assertThat(sut.match(settingsThatMatchRelease)).isFalse();
	}

	private void assertAllTrue(GlobalSettings settingsThatMatchMinor, GlobalSettings settingsThatMatchMicro, GlobalSettings settingsThatMatchRelease) {
		assertThat(sut.match(settingsThatMatchMinor)).isTrue();
		assertThat(sut.match(settingsThatMatchMicro)).isTrue();
		assertThat(sut.match(settingsThatMatchRelease)).isTrue();
	}

	private GlobalSettings getBuild(String current, String target) {
		return GlobalSettings.builder()
				.currentJavaVersion(current)
				.targetJavaVersion(target)
				.build();
	}
}