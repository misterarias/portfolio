<?php
/**
 * Created by PhpStorm.
 * User: juanito
 * Date: 18/07/15
 * Time: 12:40
 */

namespace AF\ProjectBundle\Actors;


use AF\ProjectBundle\Actors\Social\Social;

class Actor {
	private $name;
	private $role;
	private $company;
	private $company_url;
	private $description;
	private $image_name;

	/**
	 * @return mixed
	 */
	public function getName() {
		return $this->name;
	}

	/**
	 * @return mixed
	 */
	public function getRole() {
		return $this->role;
	}

	/**
	 * @return mixed
	 */
	public function getCompany() {
		return $this->company;
	}

	/**
	 * @return mixed
	 */
	public function getDescription() {
		return $this->description;
	}

	/**
	 * @return mixed
	 */
	public function getImageName() {
		return $this->image_name;
	}


	/** @var  Array */
	private $socialDataArray;

	/**
	 * @return Array
	 */
	public function getSocialDataArray() {
		return $this->socialDataArray;
	}


	/**
	 * @param $type
	 * @param $url
	 */
	public function addSocial($type, $url) {
		$this->socialDataArray[] = new Social($type, $url);

		return $this;
	}


	public function __construct($name, $company, $description, $image_name) {

		$this->name = $name;
		$this->company = $company;
		$this->description = $description;
		$this->image_name = $image_name;

		$this->socialDataArray = array();
		$this->role = null;
		$this->company_url = "#";
	}

	/**
	 * @param null $company_url
	 *
	 * @return Actor
	 */
	public function setCompanyUrl($company_url) {
		$this->company_url = $company_url;

		return $this;
	}

	/**
	 * @param null $role
	 *
	 * @return Actor
	 */
	public function setRole($role) {
		$this->role = $role;

		return $this;
	}

	/**
	 * @return null
	 */
	public function getCompanyUrl() {
		return $this->company_url;
	}
}