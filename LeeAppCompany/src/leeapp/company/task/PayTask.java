package leeapp.company.task;

import cn.nukkit.Player;
import cn.nukkit.scheduler.PluginTask;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import leeapp.company.Company;
import leeapp.company.LeeAppCompany;
import me.onebone.economyapi.EconomyAPI;

public class PayTask extends PluginTask<LeeAppCompany> {
	
	private static String BASE = "[ LA_Company ] ";
	private Config company;
	private Company com = new Company();
	
	public PayTask(LeeAppCompany owner, Config company) {
		super(owner);
		this.company = company;
	}

	@Override
	public void onRun(int arg0) {
		getOwner().getServer().broadcastMessage(TextFormat.DARK_BLUE + BASE + TextFormat.DARK_PURPLE + "급여 지급시간이 되어 각 회사마다 설정된 금액의 급여가 지급됩니다.");
		for(String name : com.getCompanyList(company)){
			if((com.getOnlineList(getOwner().getServer(), name, company) * com.getAmount(name, company)) > com.getFund(name, company)){
				for(String user : com.getList(name, company)){
					Player player = getOwner().getServer().getPlayer(user);
					if(player != null && !com.isOwner(player, company)){
						com.reduceFund(com.getAmount(name, company), name, company);
						EconomyAPI.getInstance().addMoney(player, com.getAmount(name, company));
						player.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.RED + "회사에서 급여지급시간이 되어 급여를 받았습니다. ( " + com.getAmount(name, company) + "원 )");
					}
				}
			}else{
				getOwner().getServer().broadcastMessage(TextFormat.DARK_BLUE + BASE + TextFormat.RED + name + "회사는 자금이 부족하여 급여가 지급되지 않습니다!");
			}
		}
	}


}
