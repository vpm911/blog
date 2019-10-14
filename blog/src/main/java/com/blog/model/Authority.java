package com.blog.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * An Autority used by spring security as Role
 * 
 * @author vishal.maradkar
 *
 */
@Entity
@Table(name = "authority")
public class Authority implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@NotNull
	@Column(length = 50)
	@Size(max = 50)
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Authority)) {
			return false;
		}
		return Objects.equals(name, ((Authority) obj).getName());
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

}
