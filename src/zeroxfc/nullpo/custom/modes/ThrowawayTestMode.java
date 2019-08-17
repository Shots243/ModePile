package zeroxfc.nullpo.custom.modes;

import mu.nu.nullpo.game.component.Controller;
import mu.nu.nullpo.game.component.Piece;
import mu.nu.nullpo.game.event.EventReceiver;
import mu.nu.nullpo.game.play.GameEngine;
import mu.nu.nullpo.util.CustomProperties;
import mu.nu.nullpo.util.GeneralUtil;
import zeroxfc.nullpo.custom.libs.ExamSpinner;
import zeroxfc.nullpo.custom.libs.backgroundtypes.*;

public class ThrowawayTestMode extends MarathonModeBase {
	private int initialBG, initialFadeBG;
	// private BackgroundCircularRipple backgroundCircularRipple;
	private BackgroundTGM3Style TIbg;
	private ExamSpinner es;
	private int passframe;

	/*
	 * Mode name
	 */
	@Override
	public String getName() {
		return "THROWAWAY";
	}

	/*
	 * Initialization
	 */
	@Override
	public void playerInit(GameEngine engine, int playerID) {
		owner = engine.owner;
		receiver = engine.owner.receiver;
		lastscore = 0;
		scgettime = 0;
		lastevent = EVENT_NONE;
		lastb2b = false;
		lastcombo = 0;
		lastpiece = 0;

		initialBG = AnimatedBackgroundHook.getBGState(owner);
		initialFadeBG = AnimatedBackgroundHook.getFadeBGState(owner);

		rankingRank = -1;
		rankingScore = new int[RANKING_TYPE][RANKING_MAX];
		rankingLines = new int[RANKING_TYPE][RANKING_MAX];
		rankingTime = new int[RANKING_TYPE][RANKING_MAX];

		netPlayerInit(engine, playerID);

		if(!owner.replayMode) {
			loadSetting(owner.modeConfig);
			loadRanking(owner.modeConfig, engine.ruleopt.strRuleName);
			version = CURRENT_VERSION;
		} else {
			loadSetting(owner.replayProp);
			if((version == 0) && (owner.replayProp.getProperty("throwaway.endless", false))) goaltype = 2;

			// NET: Load name
			netPlayerName = engine.owner.replayProp.getProperty(playerID + ".net.netPlayerName", "");
		}

		// backgroundCircularRipple = new BackgroundCircularRipple(engine, startlevel,8, 8,320, 240, 40f, 12,-1,1f,0.75f);
		TIbg = new BackgroundTGM3Style("res/graphics/bg0000.png", 0);
		passframe = 0;

		engine.owner.backgroundStatus.bg = startlevel;
		engine.framecolor = GameEngine.FRAME_COLOR_GREEN;
	}

	/*
	 * Called at settings screen
	 */
	@Override
	public boolean onSetting(GameEngine engine, int playerID) {
		// AnimatedBackgroundHook.setBGState(receiver, true);
		TIbg.reset();
		TIbg.setBG("res/graphics/bg0000.png");

		// NET: Net Ranking
		if(netIsNetRankingDisplayMode) {
			netOnUpdateNetPlayRanking(engine, goaltype);
		}
		// Menu
		else if(engine.owner.replayMode == false) {
			// Configuration changes
			int change = updateCursor(engine, 8, playerID);

			if(change != 0) {
				engine.playSE("change");

				switch(engine.statc[2]) {
					case 0:
						startlevel += change;
						if(tableGameClearLines[goaltype] >= 0) {
							if(startlevel < 0) startlevel = (tableGameClearLines[goaltype] - 1) / 10;
							if(startlevel > (tableGameClearLines[goaltype] - 1) / 10) startlevel = 0;
						} else {
							if(startlevel < 0) startlevel = 19;
							if(startlevel > 19) startlevel = 0;
						}
						engine.owner.backgroundStatus.bg = startlevel;
						break;
					case 1:
						//enableTSpin = !enableTSpin;
						tspinEnableType += change;
						if(tspinEnableType < 0) tspinEnableType = 2;
						if(tspinEnableType > 2) tspinEnableType = 0;
						break;
					case 2:
						enableTSpinKick = !enableTSpinKick;
						break;
					case 3:
						spinCheckType += change;
						if(spinCheckType < 0) spinCheckType = 1;
						if(spinCheckType > 1) spinCheckType = 0;
						break;
					case 4:
						tspinEnableEZ = !tspinEnableEZ;
						break;
					case 5:
						enableB2B = !enableB2B;
						break;
					case 6:
						enableCombo = !enableCombo;
						break;
					case 7:
						goaltype += change;
						if(goaltype < 0) goaltype = GAMETYPE_MAX - 1;
						if(goaltype > GAMETYPE_MAX - 1) goaltype = 0;

						if((startlevel > (tableGameClearLines[goaltype] - 1) / 10) && (tableGameClearLines[goaltype] >= 0)) {
							startlevel = (tableGameClearLines[goaltype] - 1) / 10;
							engine.owner.backgroundStatus.bg = startlevel;
						}
						break;
					case 8:
						big = !big;
						break;
				}

				// NET: Signal options change
				if(netIsNetPlay && (netNumSpectators > 0)) {
					netSendOptions(engine);
				}
			}

			// Confirm
			if(engine.ctrl.isPush(Controller.BUTTON_A) && (engine.statc[3] >= 5)) {
				// backgroundCircularRipple.setBG(startlevel);
				engine.playSE("decide");
				saveSetting(owner.modeConfig);
				receiver.saveModeConfig(owner.modeConfig);

				// NET: Signal start of the game
				if(netIsNetPlay) netLobby.netPlayerClient.send("start1p\n");

				return false;
			}

			// Cancel
			if(engine.ctrl.isPush(Controller.BUTTON_B) && !netIsNetPlay) {
				engine.quitflag = true;
			}

			// NET: Netplay Ranking
			if(engine.ctrl.isPush(Controller.BUTTON_D) && netIsNetPlay && startlevel == 0 && !big &&
					engine.ai == null) {
				netEnterNetPlayRankingScreen(engine, playerID, goaltype);
			}

			engine.statc[3]++;
		}
		// Replay
		else {
			engine.statc[3]++;
			engine.statc[2] = -1;

			if(engine.statc[3] >= 60) {
				// backgroundCircularRipple.setBG(startlevel);
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean onReady(GameEngine engine, int playerID) {
		if (engine.statc[0] == 0) {
			// backgroundCircularRipple.setBG(startlevel);
			TIbg.setSeed(engine.randSeed);
		}

		return false;
	}

	@Override
	public void onLast(GameEngine engine, int playerID) {
		super.onLast(engine, playerID);
		initialBG = AnimatedBackgroundHook.getBGState(owner);
		initialFadeBG = AnimatedBackgroundHook.getFadeBGState(owner);

		if( (engine.stat == GameEngine.STAT_SETTING) || ((engine.stat == GameEngine.STAT_RESULT) && (!owner.replayMode)) ) {
			engine.owner.backgroundStatus.bg = initialBG;
			engine.owner.backgroundStatus.fadebg = initialFadeBG;
			TIbg.reset();
		} else {
			// backgroundCircularRipple.modifyValues(null,null,1f * ((float)(engine.statistics.level + 1) / 20f),null, null);

			if (engine.stat == GameEngine.STAT_RESULT) {
				engine.owner.backgroundStatus.bg = initialBG;
				engine.owner.backgroundStatus.fadebg = initialFadeBG;
			} else {
				engine.owner.backgroundStatus.bg = -1;
				engine.owner.backgroundStatus.fadebg = -1;
			}

			TIbg.update();
		}
	}

	/*
	 * Hard drop
	 */
	@Override
	public void afterHardDropFall(GameEngine engine, int playerID, int fall) {
		engine.statistics.scoreFromHardDrop += fall * 2;
		engine.statistics.score += fall * 2;

//		int x = (16 * engine.nowPieceX) + 12 + receiver.getFieldDisplayPositionX(engine, playerID) + (16 * engine.nowPieceObject.getWidth() / 2);
//		int y = (16 * engine.nowPieceY) + 60 + receiver.getFieldDisplayPositionY(engine, playerID) + (16 * engine.nowPieceObject.getHeight() / 2);;
//
//		backgroundCircularRipple.manualRipple(x, y);
	}

	/*
	 * Calculate score
	 */
	@Override
	public void calcScore(GameEngine engine, int playerID, int lines) {
		// Line clear bonus
		int pts = 0;

		if(engine.tspin) {
			// T-Spin 0 lines
			if((lines == 0) && (!engine.tspinez)) {
				if(engine.tspinmini) {
					pts += 100 * (engine.statistics.level + 1);
					lastevent = EVENT_TSPIN_ZERO_MINI;
				} else {
					pts += 400 * (engine.statistics.level + 1);
					lastevent = EVENT_TSPIN_ZERO;
				}
			}
			// Immobile EZ Spin
			else if(engine.tspinez && (lines > 0)) {
				if(engine.b2b) {
					pts += 180 * (engine.statistics.level + 1);
				} else {
					pts += 120 * (engine.statistics.level + 1);
				}
				lastevent = EVENT_TSPIN_EZ;
			}
			// T-Spin 1 line
			else if(lines == 1) {
				if(engine.tspinmini) {
					if(engine.b2b) {
						pts += 300 * (engine.statistics.level + 1);
					} else {
						pts += 200 * (engine.statistics.level + 1);
					}
					lastevent = EVENT_TSPIN_SINGLE_MINI;
				} else {
					if(engine.b2b) {
						pts += 1200 * (engine.statistics.level + 1);
					} else {
						pts += 800 * (engine.statistics.level + 1);
					}
					lastevent = EVENT_TSPIN_SINGLE;
				}
			}
			// T-Spin 2 lines
			else if(lines == 2) {
				if(engine.tspinmini && engine.useAllSpinBonus) {
					if(engine.b2b) {
						pts += 600 * (engine.statistics.level + 1);
					} else {
						pts += 400 * (engine.statistics.level + 1);
					}
					lastevent = EVENT_TSPIN_DOUBLE_MINI;
				} else {
					if(engine.b2b) {
						pts += 1800 * (engine.statistics.level + 1);
					} else {
						pts += 1200 * (engine.statistics.level + 1);
					}
					lastevent = EVENT_TSPIN_DOUBLE;
				}
			}
			// T-Spin 3 lines
			else if(lines >= 3) {
				if(engine.b2b) {
					pts += 2400 * (engine.statistics.level + 1);
				} else {
					pts += 1600 * (engine.statistics.level + 1);
				}
				lastevent = EVENT_TSPIN_TRIPLE;
			}
		} else {
			if(lines == 1) {
				pts += 100 * (engine.statistics.level + 1); // 1列
				lastevent = EVENT_SINGLE;
			} else if(lines == 2) {
				pts += 300 * (engine.statistics.level + 1); // 2列
				lastevent = EVENT_DOUBLE;
			} else if(lines == 3) {
				pts += 500 * (engine.statistics.level + 1); // 3列
				lastevent = EVENT_TRIPLE;
			} else if(lines >= 4) {
				// 4 lines
				if(engine.b2b) {
					pts += 1200 * (engine.statistics.level + 1);
				} else {
					pts += 800 * (engine.statistics.level + 1);
				}
				lastevent = EVENT_FOUR;
			}
		}

		lastb2b = engine.b2b;

		// Combo
		if((enableCombo) && (engine.combo >= 1) && (lines >= 1)) {
			pts += ((engine.combo - 1) * 50) * (engine.statistics.level + 1);
			lastcombo = engine.combo;
		}

		// All clear
		if((lines >= 1) && (engine.field.isEmpty())) {
			engine.playSE("bravo");
			pts += 1800 * (engine.statistics.level + 1);
		}

		// Add to score
		if(pts > 0) {
			lastscore = pts;
			lastpiece = engine.nowPieceObject.id;
			scgettime = 0;
			if(lines >= 1) engine.statistics.scoreFromLineClear += pts;
			else engine.statistics.scoreFromOtherBonus += pts;
			engine.statistics.score += pts;
		}

		// BGM fade-out effects and BGM changes
		if(tableBGMChange[bgmlv] != -1) {
			if(engine.statistics.lines >= tableBGMChange[bgmlv] - 5) owner.bgmStatus.fadesw = true;

			if( (engine.statistics.lines >= tableBGMChange[bgmlv]) &&
					((engine.statistics.lines < tableGameClearLines[goaltype]) || (tableGameClearLines[goaltype] < 0)) )
			{
				bgmlv++;
				owner.bgmStatus.bgm = bgmlv;
				owner.bgmStatus.fadesw = false;
			}
		}

		// Meter
		engine.meterValue = ((engine.statistics.lines % 10) * receiver.getMeterMax(engine)) / 9;
		engine.meterColor = GameEngine.METER_COLOR_GREEN;
		if(engine.statistics.lines % 10 >= 4) engine.meterColor = GameEngine.METER_COLOR_YELLOW;
		if(engine.statistics.lines % 10 >= 6) engine.meterColor = GameEngine.METER_COLOR_ORANGE;
		if(engine.statistics.lines % 10 >= 8) engine.meterColor = GameEngine.METER_COLOR_RED;

		// backgroundCircularRipple.modifyValues(null, null, null, null, null);

		if((engine.statistics.lines >= tableGameClearLines[goaltype]) && (tableGameClearLines[goaltype] >= 0)) {
			// Ending
			engine.ending = 1;
			engine.gameEnded();
		} else if((engine.statistics.lines >= (engine.statistics.level + 1) * 10) && (engine.statistics.level < 19)) {
			// Level up
			engine.statistics.level++;

			// owner.backgroundStatus.fadesw = true;
			// owner.backgroundStatus.fadecount = 0;
			// owner.backgroundStatus.fadebg = engine.statistics.level;
			// backgroundCircularRipple.setBG(engine.statistics.level);
			initialBG = engine.statistics.level;
			initialFadeBG = engine.statistics.level;

			if (engine.statistics.level == 10) {
				TIbg.setBG("res/graphics/bg0100.png");
			}

			setSpeed(engine);
			engine.playSE("levelup");
		}
	}

	@Override
	public void renderFirst(GameEngine engine, int playerID) {
		if( !((engine.stat == GameEngine.STAT_SETTING) || ((engine.stat == GameEngine.STAT_RESULT) && (owner.replayMode == false))) ) {
			TIbg.draw(engine, playerID);
		}
	}

	/*
	 * Render score
	 */
	@Override
	public void renderLast(GameEngine engine, int playerID) {
		if (owner.menuOnly) return;

		receiver.drawScoreFont(engine, playerID, 0, 0, "BG TESTER MODE", EventReceiver.COLOR_GREEN);

		if (tableGameClearLines[goaltype] == -1) {
			receiver.drawScoreFont(engine, playerID, 0, 1, "(ENDLESS GAME)", EventReceiver.COLOR_GREEN);
		} else {
			receiver.drawScoreFont(engine, playerID, 0, 1, "(" + tableGameClearLines[goaltype] + " LINES GAME)", EventReceiver.COLOR_GREEN);
		}

		if ((engine.stat == GameEngine.STAT_SETTING) || ((engine.stat == GameEngine.STAT_RESULT) && (owner.replayMode == false))) {
			if ((owner.replayMode == false) && (big == false) && (engine.ai == null)) {
				float scale = (receiver.getNextDisplayType() == 2) ? 0.5f : 1.0f;
				int topY = (receiver.getNextDisplayType() == 2) ? 6 : 4;
				receiver.drawScoreFont(engine, playerID, 3, topY - 1, "SCORE  LINE TIME", EventReceiver.COLOR_BLUE, scale);

				for (int i = 0; i < RANKING_MAX; i++) {
					receiver.drawScoreFont(engine, playerID, 0, topY + i, String.format("%2d", i + 1), EventReceiver.COLOR_YELLOW, scale);
					receiver.drawScoreFont(engine, playerID, 3, topY + i, String.valueOf(rankingScore[goaltype][i]), (i == rankingRank), scale);
					receiver.drawScoreFont(engine, playerID, 10, topY + i, String.valueOf(rankingLines[goaltype][i]), (i == rankingRank), scale);
					receiver.drawScoreFont(engine, playerID, 15, topY + i, GeneralUtil.getTime(rankingTime[goaltype][i]), (i == rankingRank), scale);
				}
			}
		} else {
			receiver.drawScoreFont(engine, playerID, 0, 3, "SCORE", EventReceiver.COLOR_BLUE);
			String strScore;
			if ((lastscore == 0) || (scgettime >= 120)) {
				strScore = String.valueOf(engine.statistics.score);
			} else {
				strScore = String.valueOf(engine.statistics.score) + "(+" + String.valueOf(lastscore) + ")";
			}
			receiver.drawScoreFont(engine, playerID, 0, 4, strScore);

			receiver.drawScoreFont(engine, playerID, 0, 6, "LINE", EventReceiver.COLOR_BLUE);
			if ((engine.statistics.level >= 19) && (tableGameClearLines[goaltype] < 0))
				receiver.drawScoreFont(engine, playerID, 0, 7, engine.statistics.lines + "");
			else
				receiver.drawScoreFont(engine, playerID, 0, 7, engine.statistics.lines + "/" + ((engine.statistics.level + 1) * 10));

			receiver.drawScoreFont(engine, playerID, 0, 9, "LEVEL", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 0, 10, String.valueOf(engine.statistics.level + 1));

			receiver.drawScoreFont(engine, playerID, 0, 12, "TIME", EventReceiver.COLOR_BLUE);
			receiver.drawScoreFont(engine, playerID, 0, 13, GeneralUtil.getTime(engine.statistics.time));

			if ((lastevent != EVENT_NONE) && (scgettime < 120)) {
				String strPieceName = Piece.getPieceName(lastpiece);

				switch (lastevent) {
					case EVENT_SINGLE:
						receiver.drawMenuFont(engine, playerID, 2, 21, "SINGLE", EventReceiver.COLOR_DARKBLUE);
						break;
					case EVENT_DOUBLE:
						receiver.drawMenuFont(engine, playerID, 2, 21, "DOUBLE", EventReceiver.COLOR_BLUE);
						break;
					case EVENT_TRIPLE:
						receiver.drawMenuFont(engine, playerID, 2, 21, "TRIPLE", EventReceiver.COLOR_GREEN);
						break;
					case EVENT_FOUR:
						if (lastb2b) receiver.drawMenuFont(engine, playerID, 3, 21, "FOUR", EventReceiver.COLOR_RED);
						else receiver.drawMenuFont(engine, playerID, 3, 21, "FOUR", EventReceiver.COLOR_ORANGE);
						break;
					case EVENT_TSPIN_ZERO_MINI:
						receiver.drawMenuFont(engine, playerID, 2, 21, strPieceName + "-SPIN", EventReceiver.COLOR_PURPLE);
						break;
					case EVENT_TSPIN_ZERO:
						receiver.drawMenuFont(engine, playerID, 2, 21, strPieceName + "-SPIN", EventReceiver.COLOR_PINK);
						break;
					case EVENT_TSPIN_SINGLE_MINI:
						if (lastb2b)
							receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-MINI-S", EventReceiver.COLOR_RED);
						else
							receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-MINI-S", EventReceiver.COLOR_ORANGE);
						break;
					case EVENT_TSPIN_SINGLE:
						if (lastb2b)
							receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-SINGLE", EventReceiver.COLOR_RED);
						else
							receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-SINGLE", EventReceiver.COLOR_ORANGE);
						break;
					case EVENT_TSPIN_DOUBLE_MINI:
						if (lastb2b)
							receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-MINI-D", EventReceiver.COLOR_RED);
						else
							receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-MINI-D", EventReceiver.COLOR_ORANGE);
						break;
					case EVENT_TSPIN_DOUBLE:
						if (lastb2b)
							receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-DOUBLE", EventReceiver.COLOR_RED);
						else
							receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-DOUBLE", EventReceiver.COLOR_ORANGE);
						break;
					case EVENT_TSPIN_TRIPLE:
						if (lastb2b)
							receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-TRIPLE", EventReceiver.COLOR_RED);
						else
							receiver.drawMenuFont(engine, playerID, 1, 21, strPieceName + "-TRIPLE", EventReceiver.COLOR_ORANGE);
						break;
					case EVENT_TSPIN_EZ:
						if (lastb2b)
							receiver.drawMenuFont(engine, playerID, 3, 21, "EZ-" + strPieceName, EventReceiver.COLOR_RED);
						else
							receiver.drawMenuFont(engine, playerID, 3, 21, "EZ-" + strPieceName, EventReceiver.COLOR_ORANGE);
						break;
				}

				if ((lastcombo >= 2) && (lastevent != EVENT_TSPIN_ZERO_MINI) && (lastevent != EVENT_TSPIN_ZERO))
					receiver.drawMenuFont(engine, playerID, 2, 22, (lastcombo - 1) + "COMBO", EventReceiver.COLOR_CYAN);
			}
		}
	}

	@Override
	public boolean onGameOver(GameEngine engine, int playerID) {
		if (engine.statc[0] == 0)  {
			passframe = 780;

			boolean close = false;
			int p = 1;
			int diff = engine.statistics.score - 400000;
			if (Math.abs(diff) <= 50000) close = true;
			if (diff >= 0) p = 0;
			es = new ExamSpinner("GOOD", p, close);
		}
		return false;
	}

	/*
	 * 結果画面
	 */
	@Override
	public void renderResult(GameEngine engine, int playerID) {
		if(passframe > 0) {
			es.draw(receiver, engine, playerID,passframe % 2 == 0);
		} else {
			drawResultStats(engine, playerID, receiver, 0, EventReceiver.COLOR_BLUE,
					STAT_SCORE, STAT_LINES, STAT_LEVEL, STAT_TIME, STAT_SPL, STAT_LPM);
			drawResultRank(engine, playerID, receiver, 12, EventReceiver.COLOR_BLUE, rankingRank);
			drawResultNetRank(engine, playerID, receiver, 14, EventReceiver.COLOR_BLUE, netRankingRank[0]);
			drawResultNetRankDaily(engine, playerID, receiver, 16, EventReceiver.COLOR_BLUE, netRankingRank[1]);

			if(netIsPB) {
				receiver.drawMenuFont(engine, playerID, 2, 21, "NEW PB", EventReceiver.COLOR_ORANGE);
			}

			if(netIsNetPlay && (netReplaySendStatus == 1)) {
				receiver.drawMenuFont(engine, playerID, 0, 22, "SENDING...", EventReceiver.COLOR_PINK);
			} else if(netIsNetPlay && !netIsWatch && (netReplaySendStatus == 2)) {
				receiver.drawMenuFont(engine, playerID, 1, 22, "A: RETRY", EventReceiver.COLOR_RED);
			}
		}
	}

	/*
	 * 結果画面の処理
	 */
	@Override
	public boolean onResult(GameEngine engine, int playerID) {
		if(passframe > 0) {
			engine.allowTextRenderByReceiver = false; // Turn off RETRY/END menu

			es.update(engine);

			passframe--;
			return true;
		}

		engine.allowTextRenderByReceiver = true;

		return false;
	}

	/*
	 * Called when saving replay
	 */
	@Override
	public void saveReplay(GameEngine engine, int playerID, CustomProperties prop) {
		saveSetting(prop);

		// NET: Save name
		if((netPlayerName != null) && (netPlayerName.length() > 0)) {
			prop.setProperty(playerID + ".net.netPlayerName", netPlayerName);
		}

		// Update rankings
		if((!owner.replayMode) && (!big) && (engine.ai == null)) {
			updateRanking(engine.statistics.score, engine.statistics.lines, engine.statistics.time, goaltype);

			if(rankingRank != -1) {
				saveRanking(owner.modeConfig, engine.ruleopt.strRuleName);
				receiver.saveModeConfig(owner.modeConfig);
			}
		}
	}

	/**
	 * Load settings from property file
	 * @param prop Property file
	 */
	private void loadSetting(CustomProperties prop) {
		startlevel = prop.getProperty("throwaway.startlevel", 0);
		tspinEnableType = prop.getProperty("throwaway.tspinEnableType", 1);
		enableTSpin = prop.getProperty("throwaway.enableTSpin", true);
		enableTSpinKick = prop.getProperty("throwaway.enableTSpinKick", true);
		spinCheckType = prop.getProperty("throwaway.spinCheckType", 0);
		tspinEnableEZ = prop.getProperty("throwaway.tspinEnableEZ", false);
		enableB2B = prop.getProperty("throwaway.enableB2B", true);
		enableCombo = prop.getProperty("throwaway.enableCombo", true);
		goaltype = prop.getProperty("throwaway.gametype", 0);
		big = prop.getProperty("throwaway.big", false);
		version = prop.getProperty("throwaway.version", 0);
	}

	/**
	 * Save settings to property file
	 * @param prop Property file
	 */
	private void saveSetting(CustomProperties prop) {
		prop.setProperty("throwaway.startlevel", startlevel);
		prop.setProperty("throwaway.tspinEnableType", tspinEnableType);
		prop.setProperty("throwaway.enableTSpin", enableTSpin);
		prop.setProperty("throwaway.enableTSpinKick", enableTSpinKick);
		prop.setProperty("throwaway.spinCheckType", spinCheckType);
		prop.setProperty("throwaway.tspinEnableEZ", tspinEnableEZ);
		prop.setProperty("throwaway.enableB2B", enableB2B);
		prop.setProperty("throwaway.enableCombo", enableCombo);
		prop.setProperty("throwaway.gametype", goaltype);
		prop.setProperty("throwaway.big", big);
		prop.setProperty("throwaway.version", version);
	}

	/**
	 * Read rankings from property file
	 * @param prop Property file
	 * @param ruleName Rule name
	 */
	@Override
	protected void loadRanking(CustomProperties prop, String ruleName) {
		for(int i = 0; i < RANKING_MAX; i++) {
			for(int j = 0; j < GAMETYPE_MAX; j++) {
				rankingScore[j][i] = prop.getProperty("throwaway.ranking." + ruleName + "." + j + ".score." + i, 0);
				rankingLines[j][i] = prop.getProperty("throwaway.ranking." + ruleName + "." + j + ".lines." + i, 0);
				rankingTime[j][i] = prop.getProperty("throwaway.ranking." + ruleName + "." + j + ".time." + i, 0);
			}
		}
	}

	/**
	 * Save rankings to property file
	 * @param prop Property file
	 * @param ruleName Rule name
	 */
	private void saveRanking(CustomProperties prop, String ruleName) {
		for(int i = 0; i < RANKING_MAX; i++) {
			for(int j = 0; j < GAMETYPE_MAX; j++) {
				prop.setProperty("throwaway.ranking." + ruleName + "." + j + ".score." + i, rankingScore[j][i]);
				prop.setProperty("throwaway.ranking." + ruleName + "." + j + ".lines." + i, rankingLines[j][i]);
				prop.setProperty("throwaway.ranking." + ruleName + "." + j + ".time." + i, rankingTime[j][i]);
			}
		}
	}

	/**
	 * Update rankings
	 * @param sc Score
	 * @param li Lines
	 * @param time Time
	 */
	private void updateRanking(int sc, int li, int time, int type) {
		rankingRank = checkRanking(sc, li, time, type);

		if(rankingRank != -1) {
			// Shift down ranking entries
			for(int i = RANKING_MAX - 1; i > rankingRank; i--) {
				rankingScore[type][i] = rankingScore[type][i - 1];
				rankingLines[type][i] = rankingLines[type][i - 1];
				rankingTime[type][i] = rankingTime[type][i - 1];
			}

			// Add new data
			rankingScore[type][rankingRank] = sc;
			rankingLines[type][rankingRank] = li;
			rankingTime[type][rankingRank] = time;
		}
	}

	/**
	 * Calculate ranking position
	 * @param sc Score
	 * @param li Lines
	 * @param time Time
	 * @return Position (-1 if unranked)
	 */
	private int checkRanking(int sc, int li, int time, int type) {
		for(int i = 0; i < RANKING_MAX; i++) {
			if(sc > rankingScore[type][i]) {
				return i;
			} else if((sc == rankingScore[type][i]) && (li > rankingLines[type][i])) {
				return i;
			} else if((sc == rankingScore[type][i]) && (li == rankingLines[type][i]) && (time < rankingTime[type][i])) {
				return i;
			}
		}

		return -1;
	}
}
