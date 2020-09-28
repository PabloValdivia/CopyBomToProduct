package one.stabilis.cpbomtopr;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.compiere.process.ProcessInfoParameter;
import org.compiere.model.MProductBOM;
import org.compiere.model.Query;

import one.stabilis.base.CustomProcess;

/*
 * Enable the process to copy BOM lines as well. 
 * This is important in manufacturing 
 * because it gives users the ability to create a 'template' product 
 * that represents a base bill of material (BOM). 
 * Users can now quickly copy the template product's BOM into a new product 
 * and tweak as needed without re-creating the entire BOM line-for-line. 
 */

public class CopyBomToProduct extends CustomProcess {

	private int m_copyFromId;

	@Override
	protected void prepare() {

		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++) {
			String name = para[i].getParameterName();
			if (para[i].getParameter() == null)
				;
			else if (name.equals("C_CopyFrom_ID"))
				m_copyFromId = para[i].getParameterAsInt();
			else if (name.equals("M_Product_ID"))
				m_copyFromId = para[i].getParameterAsInt();
			else
				log.log(Level.SEVERE, "prepare - Unknown Parameter: " + name);
		}

	}

	/**
	 * Copy BOM Lines of Product
	 * 
	 */
	@Override
	protected String doIt() throws Exception {

		int toMProductID = getRecord_ID();
		if (log.isLoggable(Level.INFO))
			log.info("From M_Product_ID=" + m_copyFromId + " to " + toMProductID);
		if (toMProductID == 0)
			throw new IllegalArgumentException("Target M_Product_ID == 0");
		if (m_copyFromId == 0)
			throw new IllegalArgumentException("Source M_Product_ID == 0");

		// Copy bom
		List<MProductBOM> bomList = new Query(getCtx(), MProductBOM.Table_Name, "M_Product_ID=?", get_TrxName())
				.setParameters(new Object[] { m_copyFromId }).setOnlyActiveRecords(true).list();

		MProductBOM bomSrc;
		MProductBOM bomDst;
		for (Iterator<MProductBOM> it = bomList.iterator(); it.hasNext();) {
			bomSrc = it.next();
			bomDst = new MProductBOM(getCtx(), 0, get_TrxName());
			bomDst.setM_Product_ID(toMProductID);
			bomDst.setM_ProductBOM_ID(bomSrc.getM_ProductBOM_ID());
			bomDst.setLine(bomSrc.getLine());
			bomDst.setDescription(bomSrc.getDescription());
			bomDst.setBOMType(bomSrc.getBOMType());
			bomDst.setBOMQty(bomSrc.getBOMQty());
			bomDst.saveEx(get_TrxName());
		}

		int count = bomList.size();
		return "@Copied@=" + count;
	}

}