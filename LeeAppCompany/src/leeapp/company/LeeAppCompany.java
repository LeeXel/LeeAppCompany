package leeapp.company;

import java.io.File;
import java.util.HashMap;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.Listener;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import leeapp.company.task.InviteTask;
import leeapp.company.task.PayTask;
import me.onebone.economyapi.EconomyAPI;

public class LeeAppCompany extends PluginBase implements Listener{
	
	private Company com = new Company();
	private Config company;
	public HashMap<String, String> invite = new HashMap<>();
	private static String BASE = "[ LA_Company ] ";
	
	public String getCompany(Player user){
		return com.getUserCompany(user, company);
	}
	
	public String[] getCompanyList(){
		return com.getCompanyList(company);
	}
	
	public boolean isOwner(Player user){
		return com.isOwner(user, company);
	}
	
	public boolean isSubOwner(Player user){
		return com.isSubOwner(user, company);
	}
	
	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		getServer().getLogger().info("LeeAppcompany onEnable");
		getDataFolder().mkdirs();
		company = new Config(new File(getDataFolder(), "company.yml"), Config.YAML);
		company.save();
		if(!company.getAll().containsKey("createMoney")){
			company.set("createMoney", 100000);
			company.save();
		}
		if(!company.getAll().containsKey("time")){
			company.set("time", 120);
			company.save();
		}
		getServer().getScheduler().scheduleRepeatingTask(new PayTask(this, company), (company.getInt("time") * 20 * 60));
	}
	
	public long getNumber(String s){
		try {
			return Long.parseLong(s);
		} catch (Exception e) {
			return -1;
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if(command.getName().equals("회사")){
			if(args.length == 0){
				sender.sendMessage("/회사 정보 < 회사이름 >");
				sender.sendMessage("/회사 생성 < 회사이름 > ( *회사 생성비용 10만원 )");
				sender.sendMessage("/회사 자금 금액");
				sender.sendMessage("/회사 리스트");
				return false;
			}
			switch(args[0]){
			case "자금":
				if(args.length == 1){
					sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.RED + "회사에 자금으로 넣으실 금액을 입력하세요!");
					return false;
				}
				if(getNumber(args[1]) == -1){
					sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.RED + "알맞은 돈의 단위가 아닙니다!");
					return false;
				}
				if(EconomyAPI.getInstance().myMoney(sender.getName()) < getNumber(args[1])){
					sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.RED + "보유금액이 부족합니다!");
					return false;
				}
				com.addFund(getNumber(args[1]), com.getUserCompany((Player)sender, company), company);
				EconomyAPI.getInstance().reduceMoney(sender.getName(), getNumber(args[1]));
				sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.AQUA + "회사에 자금을 " + TextFormat.WHITE + getNumber(args[1]) + TextFormat.AQUA + "만큼 넣었습니다.");
				return true;
			case "정보":
				if(args.length == 1){
					sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.RED + "정보를 확인할 회사명을 입력해 주세요!");
					return false;
				}
				if(!com.containCompany(company, args[1])){
					sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.RED + "옳바른 회사명을 입력해 주세요!");
					return false;
				}
				String list = "";
				for(String li : com.getList(args[1], company)){
					list += li + ", ";
				}
				sender.sendMessage(args[1] + "회사\n- 회장: " + com.getOwner(args[1], company) + "\n- 부회장: " + com.getSubOwner(args[1], company) + "\n- 자금: " + "\n- 사원: " + list);
				break;
			case "생성":
				
				if(args.length == 1){
					sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.RED + "생성할 회사 이름을 적어주세요!");
					return false;
				}
				if(!com.getUserCompany((Player)sender, company).equals("무소속")){
					sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.RED + "이미 회사에 소속되어 있습니다!");
					return false;
				}
				if(args[1].equals("무소속")){
					sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.RED + "이 회사이름은 사용 하실수 없습니다!");
					return false;
				}
				if(com.isCompanyBeing(args[1], company)){
					sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.RED + "이미 존재하는 회사명 입니다!");
					return false;
				}
				if(EconomyAPI.getInstance().myMoney(sender.getName().toLowerCase()) < com.getCreateMoney(company)){
					sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.RED + "회사를 생성하는데 필요한 돈이 부족합니다!");
					return false;
				}
				EconomyAPI.getInstance().reduceMoney(sender.getName().toLowerCase(), com.getCreateMoney(company));
				com.createCompany(company, args[1], sender);
				sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.AQUA + args[1] + "회사를 성공적으로 생성하었습니다!");
				break;
			case "리스트":
				String li = "";
				int i = 1;
				for(String list2 : com.getCompanyList(company)){
					li += i + ". " + list2 + " ";
					i++;
				}
				sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.GREEN + "회사 리스트: \n" + li);
				break;
			case "나가기":
				if(com.getUserCompany((Player)sender, company).equals("무소속")){
					sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.RED + "회사에 소속되어 있지 않습니다!");
					return false;
				}
				String name = com.getUserCompany((Player)sender, company);
				com.delList(com.getUserCompany((Player)sender, company), (Player)sender, company);
				sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.GREEN + name + "회사에서 나가셨습니다.");
				break;
			case "수락":
				if(invite.containsKey(sender.getName().toLowerCase())){
					com.addList(invite.get(sender.getName().toLowerCase()), (Player)sender, company);
					sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.GREEN + invite.get(sender.getName().toLowerCase()) + "회사에 입사하셨습니다.");
					invite.remove(invite.get(sender.getName().toLowerCase()));
					return true;
				}
				break;
			case "거절":
				if(invite.containsKey(sender.getName().toLowerCase())){
					sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.GREEN + invite.get(sender.getName().toLowerCase()) + "회사에 초대를 거절하셨습니다.");
					invite.remove(invite.get(sender.getName().toLowerCase()));
					return true;
				}
				break;
			default:
				sender.sendMessage("/회사 정보 < 회사이름 >");
				sender.sendMessage("/회사 생성 < 회사이름 > ( *회사 생성비용 10만원 )");
				sender.sendMessage("/회사 리스트");
			}
		}
		if(command.getName().equals("회사관리")){
			if(!(com.isOwner((Player)sender, company) || com.isSubOwner((Player)sender, company))){
				sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.RED + "회장/부회장만 쓸수 있는 명령어 입니다!");
				return false;
			}
			switch(args[0]){
			case "급여":
				if(com.isSubOwner((Player)sender, company)){
					sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.RED + "회장만 쓸수 있는 명령어 입니다!");
					return false;
				}
				if(args.length == 1){
					sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.RED + "회사급여를 입력하세요!");
					return false;
				}
				if(getNumber(args[1]) == -1){
					sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.RED + "알맞은 돈의 단위가 아닙니다!");
					return false;
				}
				com.setAmount(com.getUserCompany((Player)sender, company), getNumber(args[1]), company);
				sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.AQUA + "회사급여를 " + TextFormat.WHITE + args[1] + TextFormat.AQUA + "로 설정 하셨습니다.");
				break;
			case "자금":
				if(com.isSubOwner((Player)sender, company)){
					sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.RED + "회장만 쓸수 있는 명령어 입니다!");
					return false;
				}
				if(args.length == 1){
					sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.RED + "회사자금에서 빼실 금액을 입력하세요!");
					return false;
				}
				if(getNumber(args[1]) == -1){
					sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.RED + "알맞은 돈의 단위가 아닙니다!");
					return false;
				}
				if(com.getFund(com.getUserCompany((Player)sender, company), company) < getNumber(args[1])){
					sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.RED + "회사 자금이 부족합니다!");
					return false;
				}
				com.reduceFund(getNumber(args[1]), com.getUserCompany((Player)sender, company), company);
				EconomyAPI.getInstance().addMoney((Player)sender, getNumber(args[1]));
				sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.AQUA + "회사에 자금을 " + TextFormat.WHITE + getNumber(args[1]) + TextFormat.AQUA + "만큼 뺏습니다.");
				break;
			case "부회장":
				if(com.isSubOwner((Player)sender, company)){
					sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.RED + "회장만 쓸수 있는 명령어 입니다!");
					return false;
				}
				Player user = getServer().getPlayer(args[1]);
				if(user == null){
					sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.RED + "접속하지 않는 유저 입니다!");
					return false;
				}
				if(!com.getUserCompany((Player)sender, company).equals(com.getUserCompany(user, company))){
					sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.RED + "해당 유저는 당신의 회사 소속이 아닙니다!");
					return false;
				}
				com.setSubOwner(com.getUserCompany((Player)sender, company), user, company);
				sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.GREEN + user.getName() + "를 부회장으로 지정하셨습니다!");
				break;
			case "삭제":
				if(com.isSubOwner((Player)sender, company)){
					sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.RED + "회장만 쓸수 있는 명령어 입니다!");
					return false;
				}
				getServer().getLogger().info(TextFormat.DARK_BLUE + BASE + TextFormat.RED + com.getUserCompany((Player)sender, company) + "회사가 삭제되었습니다!");
				com.removeCompany(company, com.getUserCompany((Player)sender, company));
				break;
			case "초대":
				Player user2 = getServer().getPlayer(args[1]);
				if(user2 == null){
					sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.RED + "접속하지 않는 유저 입니다!");
					return false;
				}
				invite.put(user2.getName().toLowerCase(), com.getUserCompany((Player)sender, company));
				sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.GREEN + user2.getName() + "님을 회사에 초대하셨습니다.");
				user2.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.GREEN + com.getUserCompany((Player)sender, company) + "회사에서 초대 하셨습니다. < /회사 수락or거절 >");
				getServer().getScheduler().scheduleDelayedTask(new InviteTask(this, user2), 200);
				break;
			case "강퇴":
				Player user3 = getServer().getPlayer(args[1]);
				if(user3 == null){
					sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.RED + "접속하지 않는 유저 입니다!");
					return false;
				}
				if(!com.getUserCompany(user3, company).equals(com.getUserCompany((Player)sender, company))){
					sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.RED + "해당 유저는 당신의 회사소속이 아닙니다!");
					return false;
				}
				if(com.isOwner(user3, company)){
					sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.RED + "회장은 강퇴시킬 수 없습니다!");
					return false;
				}
				com.delList(com.getUserCompany((Player)sender, company), user3, company);
				sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.GOLD + user3.getName() + "님을 강제로 퇴출 시키셨습니다.");
				break;
			default:
				sender.sendMessage("/회사관리 부회장 < 플레이어 > ( *회장만 사용가능)");
				sender.sendMessage("/회사관리 자금 금액");
				sender.sendMessage("/회사관리 삭제 ( *회장만 사용가능)");
				sender.sendMessage("/회사관리 초대 < 플레이어 >");
				sender.sendMessage("/회사관리 강퇴 < 플레이어 >");
			}
		}
		return true;
	}
}
