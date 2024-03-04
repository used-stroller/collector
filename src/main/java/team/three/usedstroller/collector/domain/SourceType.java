package team.three.usedstroller.collector.domain;

public enum SourceType {
	NAVER,
	CARROT,
	SECOND,
	BUNJANG,
	JUNGGO;

	public static SourceType findByName(String serviceName) {
		for (SourceType value : SourceType.values()) {
			if (serviceName.toUpperCase().contains(value.name())) {
				return value;
			}
		}
		return null;
	}
}
