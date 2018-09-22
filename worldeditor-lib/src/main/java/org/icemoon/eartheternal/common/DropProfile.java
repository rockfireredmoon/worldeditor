package org.icemoon.eartheternal.common;

import java.io.IOException;

@SuppressWarnings("serial")
public class DropProfile extends AbstractTableFileEntity<String, IDatabase> {
	private int QL0None;
	private int QL1Norm;
	private int QL2Unc;
	private int QL3Rare;
	private int QL4Epic;
	private int QL5Leg;
	private int QL6Art;
	private int QL0NoneL;
	private int QL1NormL;
	private int QL2UncL;
	private int QL3RareL;
	private int QL4EpicL;
	private int QL5LegL;
	private int QL6ArtL;
	private String QL0NoneF = "*";
	private String QL1NormF = "*";
	private String QL2UncF = "*";
	private String QL3RareF = "*";
	private String QL4EpicF = "*";
	private String QL5LegF = "*";
	private String QL6ArtF = "*";
	private int A0;
	private int A1;
	private int A2;
	private int A3;
	private int A4;
	private int A5;
	private int A6;

	public DropProfile() {
		super();
	}

	public DropProfile(IDatabase database) {
		super(database);
	}

	public DropProfile(IDatabase database, String shortName) {
		super(database, null, shortName);
	}

	public final int getA0() {
		return A0;
	}

	public final void setA0(int a0) {
		A0 = a0;
	}

	public final int getA1() {
		return A1;
	}

	public final void setA1(int a1) {
		A1 = a1;
	}

	public final int getA2() {
		return A2;
	}

	public final void setA2(int a2) {
		A2 = a2;
	}

	public final int getA3() {
		return A3;
	}

	public final void setA3(int a3) {
		A3 = a3;
	}

	public final int getA4() {
		return A4;
	}

	public final void setA4(int a4) {
		A4 = a4;
	}

	public final int getA5() {
		return A5;
	}

	public final void setA5(int a5) {
		A5 = a5;
	}

	public final int getA6() {
		return A6;
	}

	public final void setA6(int a6) {
		A6 = a6;
	}

	public final int getQL0None() {
		return QL0None;
	}

	public final String getQL0NoneF() {
		return QL0NoneF;
	}

	public final int getQL0NoneL() {
		return QL0NoneL;
	}

	public final int getQL1Norm() {
		return QL1Norm;
	}

	public final String getQL1NormF() {
		return QL1NormF;
	}

	public final int getQL1NormL() {
		return QL1NormL;
	}

	public final int getQL2Unc() {
		return QL2Unc;
	}

	public final String getQL2UncF() {
		return QL2UncF;
	}

	public final int getQL2UncL() {
		return QL2UncL;
	}

	public final int getQL3Rare() {
		return QL3Rare;
	}

	public final String getQL3RareF() {
		return QL3RareF;
	}

	public final int getQL3RareL() {
		return QL3RareL;
	}

	public final int getQL4Epic() {
		return QL4Epic;
	}

	public final String getQL4EpicF() {
		return QL4EpicF;
	}

	public final int getQL4EpicL() {
		return QL4EpicL;
	}

	public final int getQL5Leg() {
		return QL5Leg;
	}

	public final String getQL5LegF() {
		return QL5LegF;
	}

	public final int getQL5LegL() {
		return QL5LegL;
	}

	public final int getQL6Art() {
		return QL6Art;
	}

	public final String getQL6ArtF() {
		return QL6ArtF;
	}

	public final int getQL6ArtL() {
		return QL6ArtL;
	}

	@Override
	public void set(String[] row, String comment) {
		setEntityId(row[0]);
		QL0None = Integer.parseInt(row[1]);
		QL1Norm = Integer.parseInt(row[2]);
		QL2Unc = Integer.parseInt(row[3]);
		QL3Rare = Integer.parseInt(row[4]);
		QL4Epic = Integer.parseInt(row[5]);
		QL5Leg = Integer.parseInt(row[6]);
		QL6Art = Integer.parseInt(row[7]);
		QL0NoneL = Integer.parseInt(row[8]);
		QL1NormL = Integer.parseInt(row[9]);
		QL2UncL = Integer.parseInt(row[10]);
		QL3RareL = Integer.parseInt(row[11]);
		QL4EpicL = Integer.parseInt(row[12]);
		QL5LegL = Integer.parseInt(row[13]);
		QL6ArtL = Integer.parseInt(row[14]);
		QL0NoneF = row[15];
		QL1NormF = row[16];
		QL2UncF = row[17];
		QL3RareF = row[18];
		QL4EpicF = row[19];
		QL5LegF = row[20];
		QL6ArtF = row[21];
		if(row.length > 21) {
			A0 = Integer.parseInt(row[22]);
			A1 = Integer.parseInt(row[23]);
			A2 = Integer.parseInt(row[24]);
			A3 = Integer.parseInt(row[25]);
			A4 = Integer.parseInt(row[26]);
			A5 = Integer.parseInt(row[27]);
			A6 = Integer.parseInt(row[28]);
		}
		else {
			A0 = 1;
			A1 = 0;
			A2 = 0;
			A3 = 0;
			A4 = 0;
			A5 = 0;
			A6 = 0;
		}
	}

	public final void setQL0None(int qL0None) {
		QL0None = qL0None;
	}

	public final void setQL0NoneF(String qL0NoneF) {
		QL0NoneF = qL0NoneF;
	}

	public final void setQL0NoneL(int qL0NoneL) {
		QL0NoneL = qL0NoneL;
	}

	public final void setQL1Norm(int qL1Norm) {
		QL1Norm = qL1Norm;
	}

	public final void setQL1NormF(String qL1NormF) {
		QL1NormF = qL1NormF;
	}

	public final void setQL1NormL(int qL1NormL) {
		QL1NormL = qL1NormL;
	}

	public final void setQL2Unc(int qL2Unc) {
		QL2Unc = qL2Unc;
	}

	public final void setQL2UncF(String qL2UncF) {
		QL2UncF = qL2UncF;
	}

	public final void setQL2UncL(int qL2UncL) {
		QL2UncL = qL2UncL;
	}

	public final void setQL3Rare(int qL3Rare) {
		QL3Rare = qL3Rare;
	}

	public final void setQL3RareF(String qL3RareF) {
		QL3RareF = qL3RareF;
	}

	public final void setQL3RareL(int qL3RareL) {
		QL3RareL = qL3RareL;
	}

	public final void setQL4Epic(int qL4Epic) {
		QL4Epic = qL4Epic;
	}

	public final void setQL4EpicF(String qL4EpicF) {
		QL4EpicF = qL4EpicF;
	}

	public final void setQL4EpicL(int qL4EpicL) {
		QL4EpicL = qL4EpicL;
	}

	public final void setQL5Leg(int qL5Leg) {
		QL5Leg = qL5Leg;
	}

	public final void setQL5LegF(String qL5LegF) {
		QL5LegF = qL5LegF;
	}

	public final void setQL5LegL(int qL5LegL) {
		QL5LegL = qL5LegL;
	}

	public final void setQL6Art(int qL6Art) {
		QL6Art = qL6Art;
	}

	public final void setQL6ArtF(String qL6ArtF) {
		QL6ArtF = qL6ArtF;
	}

	public final void setQL6ArtL(int qL6ArtL) {
		QL6ArtL = qL6ArtL;
	}

	@Override
	public String toString() {
		return getEntityId();
	}

	@Override
	public void write(INIWriter writer) {
		writer.println(String.format("%s\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%d\t%d\t%d\t%d\t%d\t%d\t%d",
				getEntityId(),
				QL0None, QL1Norm, QL2Unc, QL3Rare, QL4Epic, QL5Leg, QL6Art, 
				QL0NoneL, QL1NormL, QL2UncL, QL3RareL, QL4EpicL, QL5LegL, QL6ArtL, 
				QL0NoneF, QL1NormF, QL2UncF, QL3RareF, QL4EpicF, QL5LegF, QL6ArtF,
				A0, A1, A2, A3, A4, A5, A6));
	}

	@Override
	protected void doLoad() throws IOException {
	}
}
