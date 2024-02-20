package com.tmy.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tmy.common.vo.Result;
import com.tmy.sys.entity.Role;
import com.tmy.sys.entity.Tags;
import com.tmy.sys.service.ITagsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author tmy
 * @since 2024-02-20
 */
@RestController
@RequestMapping("/tags")
public class TagsController {

    @Autowired
    private ITagsService tagsService ;

    @GetMapping("/list")
    public Result<Map<String,Object>> getTagList(@RequestParam(value = "tagName",required = false) String tagName,
                                                  @RequestParam(value = "pageNo") Long pageNo,
                                                  @RequestParam(value = "pageSize") Long pageSize){
        LambdaQueryWrapper<Tags> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasLength(tagName),Tags::getTagName,tagName);
        wrapper.orderByAsc(Tags::getTagId);

        Page<Tags> page = new Page<>(pageNo,pageSize);
        tagsService.page(page, wrapper);

        Map<String,Object> data = new HashMap<>();
        data.put("total",page.getTotal());
        data.put("rows",page.getRecords());

        return Result.success(data);
    }

    @PostMapping
    public Result<?> addTag(@RequestBody Tags tag){
        tagsService.save(tag);
        return Result.success("新增标签成功");
    }

    @PutMapping
    public Result<?> updateTag(@RequestBody Tags tag){
        if(tag.getTagId()<7){
            return Result.fail("不能修改基本标签");
        }
        tagsService.updateById(tag);
        return Result.success("修改标签成功");
    }

    @GetMapping("/{id}")
    public Result<Tags> getTagById(@PathVariable("id") Integer id){
        Tags tag = tagsService.getById(id);
        return Result.success(tag);
    }

    @DeleteMapping("/{id}")
    public Result<Tags> deleteTagById(@PathVariable("id") Integer id){
        if(id<7){
            return Result.fail("不能删除基本标签");
        }
        tagsService.removeById(id);
        return Result.success("删除标签成功");
    }

    @GetMapping("/all")
    public Result<List<Tags>> getAllTags(){
        List<Tags> tagList = tagsService.list();
        return Result.success(tagList);
    }
}
