package com.yonyou.nccloud.weixin.sdk.addr;

/**
 * @description 部门 {<br/>
 *              "name": "广州研发中心",部门名称。长度限制为1~64个字节，字符不能包括\:*?"<>｜<br/>
 *              "parentid": "1",父亲部门id。根部门id为1<br/>
 *              "order": "1",在父部门中的次序值。order值小的排序靠前。<br/>
 *              "id": "1" 部门id，整型。指定时必须大于1，不指定时则自动生成<br/>
 *              }<br/>
 */
public class Department {
	/**
	 * id
	 */
	private String id;

	/**
	 * 
	 */
	private String name;

	/**
	* 
	*/
	private String parentid;

	private String order;

	private String pname;

	public String getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String getParentid() {
		return this.parentid;
	}

	public String getPname() {
		return this.pname;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public void setParentid(String parentid) {
		this.parentid = parentid;
	}

	public void setPname(String pname) {
		this.pname = pname;
	}

}
