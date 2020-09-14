Proces umożliwiający kopiowanie linii BOM w oknie Asortyment (M_Product) jako proces dostępny tylko w menu.


# HG changeset patch
# Parent 5f5ec8fb7a3c463d55229b01bc3043f94565ac72
IDEMPIERE-2438 Copy Product Process to include BOM records

diff -r 5f5ec8fb7a3c org.adempiere.base.process/src/org/compiere/process/CopyProduct.java
--- a/org.adempiere.base.process/src/org/compiere/process/CopyProduct.java	Mon Feb 02 22:00:10 2015 -0600
+++ b/org.adempiere.base.process/src/org/compiere/process/CopyProduct.java	Mon Feb 02 22:11:53 2015 -0600
@@ -5,6 +5,7 @@
 import java.util.logging.Level;
 
 import org.compiere.model.MBPartnerProduct;
+import org.compiere.model.MProductBOM;
 import org.compiere.model.MProductDownload;
 import org.compiere.model.MProductPrice;
 import org.compiere.model.Query;
@@ -217,6 +218,28 @@
 			dlDst.saveEx(get_TrxName());
 		}
 		count += dlList.size();
+		
+		// Copy bom
+		List<MProductBOM> bomList = new Query(getCtx(), MProductBOM.Table_Name, "M_Product_ID=?", get_TrxName())
+										.setParameters(new Object[]{m_copyFromId})
+										.setOnlyActiveRecords(true)
+										.list();
+		
+		MProductBOM bomSrc;
+		MProductBOM bomDst;
+		for (Iterator<MProductBOM> it = bomList.iterator(); it.hasNext();) {
+			bomSrc = it.next();
+			bomDst = new MProductBOM(getCtx(), 0, get_TrxName());
+			bomDst.setM_Product_ID(toMProductID);
+			bomDst.setM_ProductBOM_ID(bomSrc.getM_ProductBOM_ID());
+			bomDst.setLine(bomSrc.getLine());
+			bomDst.setDescription(bomSrc.getDescription());
+			bomDst.setBOMType(bomSrc.getBOMType());
+			bomDst.setBOMQty(bomSrc.getBOMQty());
+			bomDst.saveEx(get_TrxName());
+		}
+		count += bomList.size();
+
 
 		// Don't copy accounting because of constraints.
 		/*

language-diff