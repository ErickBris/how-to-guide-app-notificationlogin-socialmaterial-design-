<?php

namespace AppBundle\Entity;

use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Validator\Constraints as Assert;
use MediaBundle\Entity\Media;
use Doctrine\Common\Collections\ArrayCollection;

/**
 * Guide
 *
 * @ORM\Table(name="guide_how_to")
 * @ORM\Entity(repositoryClass="AppBundle\Repository\GuideRepository")
 */
class Guide
{
    /**
     * @var int
     *
     * @ORM\Column(name="id", type="integer")
     * @ORM\Id
     * @ORM\GeneratedValue(strategy="AUTO")
     */
    private $id;

    /**
     * @var string
     *
     * @ORM\Column(name="title", type="string", length=255)
     * @Assert\NotBlank()
     * @Assert\Length(
     *      min = 3,
     *      max = 200,
     * )
     */
    private $title;
    /**
     * @var \DateTime
     *
     * @ORM\Column(name="created", type="datetime")
     */
    private $created;

    /**
     * @ORM\ManyToOne(targetEntity="MediaBundle\Entity\Media")
     * @ORM\JoinColumn(name="media_id", referencedColumnName="id")
     * @ORM\JoinColumn(nullable=false)
     */
    private $media;
    /**
     * @var string
     *
     * @ORM\Column(name="content", type="text")
     * @Assert\NotBlank()
     * @Assert\Length(
     *      min = 3
     * )
     */
    private $content;
    /**
     * @Assert\File(mimeTypes={"image/jpeg","image/png" },maxSize="4M")
     */
    private $file;
      /**
     * @ORM\ManyToOne(targetEntity="Category", inversedBy="guides")
     * @ORM\JoinColumn(name="category_id", referencedColumnName="id", nullable=true,onDelete="SET NULL")
     */
    private $category;
    /**
    * @ORM\OneToMany(targetEntity="Step", mappedBy="guide")
    * @ORM\OrderBy({"position" = "asc"})
    */
    private $steps;
        /**
     * @var bool
     *
     * @ORM\Column(name="enabled", type="boolean")
     */
    private $enabled;
        /**
     * @var bool
     *
     * @ORM\Column(name="comment", type="boolean")
     */
    private $comment;

    /**
    * @ORM\OneToMany(targetEntity="Comment", mappedBy="guide",cascade={"persist", "remove"})
    * @ORM\OrderBy({"created" = "asc"})
    */
    private $comments;
    public function __construct()
    {
    	$this->comments = new ArrayCollection();
    	$this->steps = new ArrayCollection();
        $this->created= new \DateTime();
    }
    /**
     * Get id
     *
     * @return integer 
     */
    public function getId()
    {
        return $this->id;
    }

    /**
     * Set title
     *
     * @param string $title
     * @return Guide
     */
    public function setTitle($title)
    {
        $this->title = $title;

        return $this;
    }

    /**
     * Get title
     *
     * @return string 
     */
    public function getTitle()
    {
        return $this->title;
    }
     /**
     * Set media
     *
     * @param string $media
     * @return Article
     */
    public function setMedia(Media $media)
    {
        $this->media = $media;

        return $this;
    }

    /**
     * Get media
     *
     * @return string 
     */
    public function getMedia()
    {
        return $this->media;
    }
    public function getFile()
    {
        return $this->file;
    }
    public function setFile($file)
    {
        $this->file = $file;
        return $this;
    }

     
     /**
     * Set created
     *
     * @param \DateTime $created
     * @return Article
     */
    public function setCreated($created)
    {
        $this->created = $created;

        return $this;
    }

    /**
     * Get created
     *
     * @return \DateTime 
     */
    public function getCreated()
    {
        return $this->created;
    }

    /**
    * Get category
    * @return  
    */
    public function getCategory()
    {
        return $this->category;
    }
    
    /**
    * Set category
    * @return $this
    */
    public function setCategory($category)
    {
        $this->category = $category;
        return $this;
    }
        /**
     * Set content
     *
     * @param string $content
     * @return Comment
     */
    public function setContent($content)
    {
        $this->content = $content;

        return $this;
    }

    /**
     * Get content
     *
     * @return string 
     */
    public function getContent()
    {
        return $this->content;
    }
     /**
     * Add steps
     *
     * @param Article $steps
     * @return Categorie
     */
    public function addStep(Step $steps)
    {
        $this->steps[] = $steps;

        return $this;
    }

    /**
     * Remove steps
     *
     * @param Comment $steps
     */
    public function removeStep(Step $steps)
    {
        $this->steps->removeElement($steps);
    }

    /**
     * Get steps
     *
     * @return \Doctrine\Common\Collections\Collection 
     */
    public function getSteps()
    {
        return $this->steps;
    }
    /**
     * Set enabled
     *
     * @param boolean $enabled
     * @return Article
     */
    public function setEnabled($enabled)
    {
        $this->enabled = $enabled;

        return $this;
    }

    /**
     * Get enabled
     *
     * @return boolean 
     */
    public function getEnabled()
    {
        return $this->enabled;
    }
    /**
    * Get comment
    * @return  
    */
    public function getComment()
    {
        return $this->comment;
    }
    
    /**
    * Set comment
    * @return $this
    */
    public function setComment($comment)
    {
        $this->comment = $comment;
        return $this;
    }
     /**
     * Add comments
     *
     * @param Article $comments
     * @return Categorie
     */
    public function addComment(Comment $comments)
    {
        $this->comments[] = $comments;

        return $this;
    }

    /**
     * Remove comments
     *
     * @param Comment $comments
     */
    public function removeComment(Comment $comments)
    {
        $this->comments->removeElement($comments);
    }

    /**
     * Get comments
     *
     * @return \Doctrine\Common\Collections\Collection 
     */
    public function getComments()
    {
        return $this->comments;
    }
}
