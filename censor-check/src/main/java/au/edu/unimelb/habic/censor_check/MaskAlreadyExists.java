package au.edu.unimelb.habic.censor_check;

public class MaskAlreadyExists extends RuntimeException {

	private static final long serialVersionUID = -1754243044301594950L;

	public MaskAlreadyExists(String record_name, Annotation existing, Annotation new_annotation) {
		super();
	}
}
