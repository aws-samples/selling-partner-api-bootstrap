package cn.amazon.aws.rp.spapi.enums;

/**
 * @description:
 * @className: StatusEnums
 * @type: JAVA
 * @date: 2020/11/11 11:57
 * @author: zhangkui
 */
public enum StatusEnum {

	INIT(0, "INIT"),
	WORKING(1, "WORKING"),
	COMPLETE(2, "COMPLETE");

	private Integer status;

	private String desc;

	StatusEnum(Integer status, String desc) {
		this.status = status;
		this.desc = desc;
	}

	public Integer getStatus() {
		return status;
	}

	public String getDesc() {
		return desc;
	}

}
