package commtest;

import com.ye.gdufs.model.Page;
import com.ye.gdufs.util.HibernateSql;
import com.ye.gdufs.util.HibernateUtil;

public class HQLTest {
	
	
	public static void main(String[] args) {
		Page p =new Page();
		p.setBodyFrequency(1);
		p.setSerName("uasdf");
		p.setTitleFrequency(0);
		p.setUid(0);
		p.setUrl("awebfksauhy");
		HibernateSql hs = session -> session.createSQLQuery("replace into page(uid,url,titlefrequency,bodyfrequency,sername)"
				+ " values(:uid,:url,:titlefrequency,:bodyfrequency,:sername)")
				.setLong("uid", p.getUid())
				.setString("url", p.getUrl())
				.setInteger("titlefrequency", p.getTitleFrequency())
				.setInteger("bodyfrequency", p.getBodyFrequency())
				.setString("sername", p.getSerName()).executeUpdate();
		try {
			HibernateUtil.execute(hs );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
