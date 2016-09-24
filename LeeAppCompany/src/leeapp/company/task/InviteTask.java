package leeapp.company.task;

import cn.nukkit.Player;
import cn.nukkit.scheduler.PluginTask;
import leeapp.company.LeeAppCompany;

public class InviteTask extends PluginTask<LeeAppCompany> {

	Player user;
	
	public InviteTask(LeeAppCompany owner, Player user) {
		super(owner);
		this.user = user;
	}

	@Override
	public void onRun(int arg0) {
		if(owner.invite.containsKey(user.getName().toLowerCase())){
			owner.invite.remove(user.getName().toLowerCase());
		}
	}
}
