package mapleGame;

import java.awt.Container;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class MapleApp extends JFrame implements Initable {
	MapleApp mapleApp = this;
	static final String TAG = "MapleApp : ";
	JLabel laBackground;
	Player player;
	PlayerHpBar bar;
	PlayerMpBar bar2;

	// 점수 변수
	static int scoreMushNum;
	static int scoreStoneNum;
	static int[] deadEnemy = { 0, 0, 0 }; // 0번지 버섯 죽인수 / 1번지 골렘 죽인 수 / 2번지 총점

	static GamePoint gamePoint;

	ArrayList<Enemy> enemy;

	// 주황버섯 객체
	Mushroom mushroom;
	// 스톤골렘 객체
	Stone stone;
	// 발록 객체
	Barlog barlog;
	// 아이스골렘 객체
	Block block;
	// 보스 객체
	Boss boss;

	boolean isBoss = false;
	boolean isBossStart = true;

	// 로그인
	LoginScr loginScr;

	Container c;
	Skill skillShot;

	// HP 라벨
	ImageIcon icHp0, icHp10, icHp20, icHp30, icHp40, icHp50, icHp60, icHp70, icHp80, icHp90, icHp100;
	// MP 라벨
	ImageIcon icMp0, icMp10, icMp20, icMp30, icMp40, icMp50;

	public MapleApp() {
		init(); // new
		setting();
		batch();
		listener();

		setVisible(true); // 마지막 고정
	}

	public MapleApp(LoginScr loginScr) {
		init(); // new
		setting();
		batch();
		listener();

		setVisible(true); // 마지막 고정
	}

	public static void main(String[] args) {
		new MapleApp();
	}

	@Override
	public void init() {
		c = getContentPane();
		laBackground = new JLabel(new ImageIcon("image/background2.png"));

		enemy = new ArrayList<>();

		icHp0 = new ImageIcon("image/hpp0.png");
		icHp10 = new ImageIcon("image/hpp10.png");
		icHp20 = new ImageIcon("image/hpp20.png");
		icHp30 = new ImageIcon("image/hpp30.png");
		icHp40 = new ImageIcon("image/hpp40.png");
		icHp50 = new ImageIcon("image/hpp50.png");
		icHp60 = new ImageIcon("image/hpp60.png");
		icHp70 = new ImageIcon("image/hpp70.png");
		icHp80 = new ImageIcon("image/hpp80.png");
		icHp90 = new ImageIcon("image/hpp90.png");
		icHp100 = new ImageIcon("image/hpp100.png");

		icMp0 = new ImageIcon("image/mpp0.png");
		icMp10 = new ImageIcon("image/mpp10.png");
		icMp20 = new ImageIcon("image/mpp20.png");
		icMp30 = new ImageIcon("image/mpp30.png");
		icMp40 = new ImageIcon("image/mpp40.png");
		icMp50 = new ImageIcon("image/mpp50.png");

		player = new Player();

		mushroom = new Mushroom("image/주황버섯오른쪽.gif", 555, 380, 30, "주황버섯");
		stone = new Stone("image/Stone.gif", 100, 150, 30, "스톤골렘");
		barlog = new Barlog("image/발록오른쪽.gif", 200, -20, 30, "주니어발록");
		block = new Block("image/블록골렘오른쪽.gif", 200, 300, 30, "블록골렘");
		boss = new Boss("image/자쿰.gif", 400, 20, 800, "자쿰");

		bar = new PlayerHpBar();
		bar2 = new PlayerMpBar();
		gamePoint = new GamePoint();

		enemy.add(mushroom);
		enemy.add(stone);
		enemy.add(barlog);
		enemy.add(block);
		enemy.add(boss);

		player.healing();

		Thread enemyCol = new Thread(new col(enemy));
		enemyCol.start();
		boss.x = 9999;
	}

	@Override
	public void setting() {
		setTitle("메이플 테스트");
		setSize(1290, 759);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setLayout(null);
		setContentPane(laBackground);
	}

	@Override
	public void batch() {
		add(player);
		add(mushroom);
		add(stone);
		add(barlog);
		add(block);
		add(bar);
		add(bar2);
		add(gamePoint);

	}

	boolean canJump = true;

	@Override
	public void listener() {
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					player.moveRight1();
				} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					player.moveLeft();
				} else if (e.getKeyCode() == KeyEvent.VK_A) {
					player.attack();
				} else if (e.getKeyCode() == KeyEvent.VK_UP) {
					if (canJump) {
						player.moveJump();
						canJump = false;

						new Thread(new Runnable() {
							@Override
							public void run() {
								try {
									Thread.sleep(1000);
									canJump = true;
								} catch (InterruptedException ex) {
									ex.printStackTrace();
								}
							}
						}).start();
					}
				} else if (player.mp >= 10) {
					if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
						skillShot = new Skill(player, enemy);
						add(skillShot);
						player.skilshot();
						System.out.println("MP : " + player.mp + " 남았습니다.");
					} else if (e.getKeyCode() == KeyEvent.VK_UP) {
						player.isJump = false;
					}
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					player.isRight = false;
					player.isMove = false;

				} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					player.isLeft = false;
					player.isMove = false;
				} else if (e.getKeyCode() == KeyEvent.VK_A) {
					player.isAttack = false;
				}
			}
		});

	}

	// 충돌 클래스
	class col extends Thread {
		ArrayList<Enemy> enemy;

		public col(ArrayList<Enemy> enemy) {
			this.enemy = enemy;
		}

		@Override
		public void run() {
			while (true) {

				try {
					for (int i = 0; i < enemy.size(); i++) {
						Thread.sleep(10);
						if (enemy.get(i) instanceof Boss && enemy.get(i).hp < 380) {
							enemy.get(i).setIcon(new ImageIcon("image/자쿰개피.gif"));

						}
						// 보스
						if (isBossStart == true) {
							if (deadEnemy[2] > 170) { // 설정한 점수 이상이면 보스맵 입장
								isBoss = true;
								mushroom.x = 99999;
								stone.x = 99999;
								block.x = 99999;
								barlog.x = 99999;
							}

							if (isBoss == true) {

								// int result = JOptionPane.showConfirmDialog(null, "보스룸입장!", "안내메세지",
								// JOptionPane.OK_OPTION);
								Thread.sleep(3100);
								laBackground.setIcon(new ImageIcon("image/자쿰의제단.png"));

								mushroom.x = 99999;
								stone.x = 99999;
								block.x = 99999;
								barlog.x = 99999;
								c.remove(block);
								c.remove(stone);
								c.remove(mushroom);
								c.remove(barlog);

								add(boss);

								boss.x = 400;

								isBossStart = false;
								isBoss = false;
								player.y = 500;
								System.out.println("실행됨??");
							}
						} //

						if (player.hp == 100) {
							bar.setIcon(icHp100);
						} else if (player.hp < 100 && player.hp >= 90) {
							bar.setIcon(icHp90);
						} else if (player.hp < 90 && player.hp >= 80) {
							bar.setIcon(icHp80);
						} else if (player.hp < 80 && player.hp >= 70) {
							bar.setIcon(icHp70);
						} else if (player.hp < 70 && player.hp >= 60) {
							bar.setIcon(icHp60);
						} else if (player.hp < 60 && player.hp >= 50) {
							bar.setIcon(icHp50);
						} else if (player.hp < 50 && player.hp >= 40) {
							bar.setIcon(icHp40);
						} else if (player.hp < 40 && player.hp >= 30) {
							bar.setIcon(icHp30);
						} else if (player.hp < 3 && player.hp >= 20) {
							bar.setIcon(icHp20);
						} else if (player.hp < 20 && player.hp >= 10) {
							bar.setIcon(icHp10);
						}

						if (player.mp == 50) {
							bar2.setIcon(icMp50);
						} else if (player.mp < 50 && player.mp >= 40) {
							bar2.setIcon(icMp40);
						} else if (player.mp < 40 && player.mp >= 30) {
							bar2.setIcon(icMp30);
						} else if (player.mp < 30 && player.mp >= 20) {
							bar2.setIcon(icMp20);
						} else if (player.mp < 20 && player.mp >= 10) {
							bar2.setIcon(icMp10);
						} else if (player.mp < 10 && player.mp >= 0) {
							bar2.setIcon(icMp0);
						}

						gamePoint.setText("Point : " + MapleApp.deadEnemy[2]); // 점수 표시

						// 플레이어
						if (crash(player.x, player.y, enemy.get(i).x, enemy.get(i).y, player.width, player.height,
								enemy.get(i).width, enemy.get(i).height)) {
							System.out.println("충돌 발생!");
							Thread.sleep(1500);
							player.hp = player.hp - 10;
							System.out.println("플레이어 hp : " + player.hp + " 남았습니다.");

							if (player.hp <= 0) {
								player.dieDown();
								int result = JOptionPane.showConfirmDialog(null,
										"죽었네... 점수는 " + MapleApp.deadEnemy[2] + "점 입니다.", "안내메세지",
										JOptionPane.OK_OPTION);
								if (result == JOptionPane.OK_OPTION) {
									System.exit(0);
								}
							}

						}
						if (player.isAttack == true) {
							// 기본공격
							if (attackCrash(player.x, player.y, enemy.get(i).x, enemy.get(i).y, player.width,
									player.height, enemy.get(i).width, enemy.get(i).height)) {
								if (player.isAttack == true) {
									System.out.println("기본공격 적중!");
									deadEnemy[2] += 5;
									enemy.get(i).moveState = 0;
									Thread.sleep(1000);
									enemy.get(i).hp = enemy.get(i).hp - 10;
									System.out.println(" hp : " + enemy.get(i).hp);

									if (enemy.get(i).hp <= 0) {
										System.out.println(enemy.get(i).name + " 죽음...");
										score(enemy.get(i).name); // 점수 계산
										gamePoint.setText("Point : " + deadEnemy[2]); // 점수 표시
										enemy.get(i).x = 999999;
										Thread.sleep(3000);
										if (enemy.get(i) instanceof Mushroom) {
											enemy.get(i).x = 550;
											enemy.get(i).hp = 20;

										}

										if (enemy.get(i) instanceof Stone) {
											enemy.get(i).x = 100;
											enemy.get(i).hp = 20;
										}
										if (enemy.get(i) instanceof Barlog) {
											enemy.get(i).x = 200;
											enemy.get(i).hp = 20;
										}
										if (enemy.get(i) instanceof Block) {
											enemy.get(i).x = 200;
											enemy.get(i).hp = 20;
										}
									}

								}

							}
						}
					}

				} catch (Exception e) {
					e.getMessage();
				}
			}

		}
	}

	// 충돌 함수
	public boolean crash(int playerX, int playerY, int enemyX, int enemyY, int playerW, int playerH, int enemyW,
			int enemyH) {
		boolean check = false;
		if (Math.abs((playerX + (playerW / 2)) - (enemyX + enemyW / 2 + 20)) < (enemyW / 2 + playerW / 2 - 60)
				&& Math.abs((playerY + playerH / 2) - (enemyY + enemyH / 2)) < (enemyH / 2 + playerH / 2)) {
			check = true;
		} else {
			check = false;
		}
		return check;
	}
	// end 충돌

	// 몬스터 공격 함수

	public boolean attackCrash(int playerX, int playerY, int enemyX, int enemyY, int playerW, int playerH, int enemyW,
			int enemyH) {
		boolean check = false;
		if (Math.abs((playerX + (playerW / 2)) - (enemyX + enemyW / 2)) < (enemyW / 2 + playerW / 2)
				&& Math.abs((playerY + playerH / 2) - (enemyY + enemyH / 2)) < (enemyH / 2 + playerH / 2)) {
			check = true;
		} else {
			check = false;
		}
		return check;
	} // 몬스터 공격 함수 END

	public static int[] score(String enemy) {

		if (enemy == "주황버섯") { // 버섯 50점
			scoreMushNum += 50; // 버섯 죽을 때마다 50점 증가
			deadEnemy[0]++; // 버섯 잡은 수 계산
			deadEnemy[2] += 50;
			System.out.println("버섯 죽음");
			System.out.println(scoreMushNum);
		} else if (enemy == "스톤골렘") { // 골렘 30점
			scoreStoneNum += 30; // 골렘 죽을 때마다 30점 증가
			deadEnemy[1]++; // 골렘 죽은 수 계산
			deadEnemy[2] += 30;
			System.out.println("골렘 죽음");
			System.out.println(scoreStoneNum);
		} else if (enemy == "주니어발록") { // 골렘 30점
			scoreStoneNum += 40; // 골렘 죽을 때마다 30점 증가
			deadEnemy[1]++; // 골렘 죽은 수 계산
			deadEnemy[2] += 40;
			System.out.println("골렘 죽음");
			System.out.println(scoreStoneNum);
		} else if (enemy == "블록골렘") { // 골렘 30점
			scoreStoneNum += 30; // 골렘 죽을 때마다 30점 증가
			deadEnemy[1]++; // 골렘 죽은 수 계산
			deadEnemy[2] += 30;
			System.out.println("블록골렘 죽음");
			System.out.println(scoreStoneNum);

		} else if (enemy == "자쿰") {
			int result = JOptionPane.showConfirmDialog(null, "축하합니다 미션완료!! " + MapleApp.deadEnemy[2] + "점 입니다.",
					"안내메세지", JOptionPane.CLOSED_OPTION);
			if (result == JOptionPane.OK_OPTION) {
				System.exit(0);
			}
		}
		return deadEnemy; // 0번지 - 버섯 죽은 수
	} // 1번지 - 골렘 죽은 수 총 몬스터 잡은 수 반환

}