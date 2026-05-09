export const ingredientGroups = [
  {
    key: 'cupType',
    title: '杯型温度',
    mode: 'single',
    options: [
      { value: 'coldCup', label: '冷杯', description: '透明玻璃、冰雾与水珠', impact: '清爽 +2' },
      { value: 'hotCup', label: '热杯', description: '陶瓷杯、蒸汽与暖光', impact: '醇厚 +1' }
    ]
  },
  {
    key: 'coffeeBase',
    title: '咖啡基底',
    mode: 'single',
    options: [
      { value: 'espresso', label: '浓缩咖啡', description: '深烘坚果与焦糖尾韵', impact: '苦味 +2' },
      { value: 'americano', label: '美式基底', description: '轻盈通透，入口干净', impact: '清爽 +2' },
      { value: 'coldBrew', label: '冷萃', description: '低酸顺滑，带可可感', impact: '醇厚 +1' },
      { value: 'decaf', label: '低因咖啡', description: '柔和低负担的夜间选择', impact: '咖啡因 -2' }
    ]
  },
  {
    key: 'espressoShots',
    title: '浓缩份数',
    mode: 'single',
    options: [
      { value: 1, label: '单份', description: '轻盈基础层', impact: '苦味 +1' },
      { value: 2, label: '双份', description: '经典平衡浓度', impact: '浓郁 +2' },
      { value: 3, label: '三份', description: '高能深烘冲击', impact: '浓郁 +3' }
    ]
  },
  {
    key: 'milkType',
    title: '奶类',
    mode: 'single',
    options: [
      { value: 'none', label: '不加奶', description: '保留咖啡原始线条', impact: '清爽 +1' },
      { value: 'wholeMilk', label: '全脂牛奶', description: '圆润奶香，甜感明显', impact: '奶香 +2' },
      { value: 'lowFatMilk', label: '低脂牛奶', description: '轻盈顺滑，不抢风味', impact: '顺滑 +1' },
      { value: 'oatMilk', label: '燕麦奶', description: '谷物甜香与坚果感', impact: '奶香 +2' },
      { value: 'coconutMilk', label: '椰奶', description: '热带白色香气', impact: '清爽 +1' },
      { value: 'thickMilk', label: '厚乳', description: '更稠密的甜润口感', impact: '醇厚 +2' }
    ]
  },
  {
    key: 'sweetness',
    title: '甜度',
    mode: 'single',
    options: [
      { value: 'noSugar', label: '无糖', description: '让咖啡苦甜自然呈现', impact: '甜度 0' },
      { value: 'lowSugar', label: '三分糖', description: '微甜收边', impact: '甜度 +1' },
      { value: 'halfSugar', label: '半糖', description: '香气与甜感平衡', impact: '甜度 +2' },
      { value: 'lessSugar', label: '七分糖', description: '更柔和的甜润曲线', impact: '甜度 +3' },
      { value: 'fullSugar', label: '全糖', description: '甜点式满足感', impact: '甜度 +4' }
    ]
  },
  {
    key: 'iceLevel',
    title: '冰量',
    mode: 'single',
    options: [
      { value: 'noIce', label: '去冰', description: '低温感轻微', impact: '清爽 0' },
      { value: 'lessIce', label: '少冰', description: '保留轻微冰感', impact: '清爽 +1' },
      { value: 'normalIce', label: '正常冰', description: '透明冰块与冷雾', impact: '清爽 +2' },
      { value: 'extraIce', label: '多冰', description: '高透冰饮氛围', impact: '清爽 +3' }
    ]
  },
  {
    key: 'syrups',
    title: '风味糖浆',
    mode: 'multi',
    options: [
      { value: 'vanilla', label: '香草', description: '浅金香草甜香', impact: '甜度 +1' },
      { value: 'caramel', label: '焦糖', description: '琥珀烘烤香', impact: '醇厚 +1' },
      { value: 'hazelnut', label: '榛果', description: '坚果奶油尾韵', impact: '浓郁 +1' },
      { value: 'mocha', label: '摩卡', description: '深巧克力气息', impact: '苦甜 +1' },
      { value: 'seaSaltCaramel', label: '海盐焦糖', description: '咸甜层次更立体', impact: '甜度 +2' }
    ]
  },
  {
    key: 'foam',
    title: '奶泡奶盖',
    mode: 'single',
    options: [
      { value: 'none', label: '不加奶泡', description: '杯面更清透', impact: '顺滑 0' },
      { value: 'lightFoam', label: '轻奶泡', description: '薄雾般柔滑覆盖', impact: '奶香 +1' },
      { value: 'thickFoam', label: '厚奶泡', description: '饱满云朵口感', impact: '奶香 +2' },
      { value: 'seaSaltCream', label: '海盐奶盖', description: '咸甜奶油冠层', impact: '醇厚 +2' },
      { value: 'coconutCloud', label: '椰子云朵', description: '轻盈椰香浮层', impact: '清爽 +1' }
    ]
  },
  {
    key: 'toppings',
    title: '顶料',
    mode: 'multi',
    options: [
      { value: 'cocoaPowder', label: '可可粉', description: '轻撒苦甜粉末', impact: '苦味 +1' },
      { value: 'cinnamon', label: '肉桂粉', description: '温暖香料气息', impact: '香气 +1' },
      { value: 'caramelCrisps', label: '焦糖脆片', description: '细碎焦糖光泽', impact: '甜度 +1' },
      { value: 'chocolateChips', label: '巧克力碎', description: '深色可可颗粒', impact: '醇厚 +1' },
      { value: 'nutCrunch', label: '坚果碎', description: '烘烤坚果层次', impact: '浓郁 +1' }
    ]
  }
]

export const defaultRecipe = {
  name: '',
  note: '',
  cupType: 'coldCup',
  temperatureType: 'cold',
  coffeeBase: 'espresso',
  espressoShots: 2,
  milkType: 'oatMilk',
  sweetness: 'halfSugar',
  iceLevel: 'normalIce',
  syrups: ['caramel'],
  foam: 'lightFoam',
  toppings: ['cocoaPowder'],
  isPublic: false
}

export const classicCoffees = [
  { id: 1, name: '拿铁', description: '浓缩与牛奶融合，柔和奶香与坚果尾韵。', caffeineLevel: 3, suitableCrowd: '喜欢顺滑奶咖的人', tags: ['奶香', '顺滑', '经典'] },
  { id: 2, name: '美式', description: '通透深烘香气，干净清爽，适合全天候。', caffeineLevel: 4, suitableCrowd: '偏爱清爽咖啡的人', tags: ['清爽', '低糖', '高因'] },
  { id: 3, name: '卡布奇诺', description: '绵密奶泡、浓缩基底与可可香形成轻盈层次。', caffeineLevel: 3, suitableCrowd: '喜欢泡沫口感的人', tags: ['奶泡', '经典', '暖香'] },
  { id: 4, name: '摩卡', description: '咖啡与巧克力交叠，甜点感更强。', caffeineLevel: 3, suitableCrowd: '喜欢可可甜香的人', tags: ['可可', '甜感', '醇厚'] },
  { id: 5, name: '焦糖玛奇朵', description: '焦糖香线条落在奶泡上，入口柔甜。', caffeineLevel: 3, suitableCrowd: '偏爱焦糖风味的人', tags: ['焦糖', '奶泡', '香甜'] },
  { id: 6, name: '冷萃', description: '低酸、圆润、冷感明显，带自然可可调。', caffeineLevel: 4, suitableCrowd: '喜欢冰饮与低酸的人', tags: ['冷饮', '低酸', '清爽'] },
  { id: 7, name: '澳白', description: '更薄奶泡与更强咖啡感，优雅而克制。', caffeineLevel: 4, suitableCrowd: '想要更浓奶咖的人', tags: ['浓郁', '奶香', '精品'] }
]

export const publicRecipes = [
  { id: 2001, recipeName: '午夜榛果拿铁', name: '午夜榛果拿铁', authorName: 'Latte Lab', cupType: 'coldCup', temperatureType: 'cold', flavorTags: ['榛果', '奶香浓郁', '冰爽'], averageRating: 4.9, ratingCount: 128, triedCount: 820, favoriteCount: 214, forkCount: 86, hotScore: 1802 },
  { id: 2002, recipeName: '海盐焦糖冷萃', name: '海盐焦糖冷萃', authorName: 'Cold Brew Kid', cupType: 'coldCup', temperatureType: 'cold', flavorTags: ['海盐焦糖', '低酸', '清爽'], averageRating: 4.8, ratingCount: 96, triedCount: 640, favoriteCount: 188, forkCount: 72, hotScore: 1488 },
  { id: 2003, recipeName: '冬日厚乳摩卡', name: '冬日厚乳摩卡', authorName: 'Mocha Muse', cupType: 'hotCup', temperatureType: 'hot', flavorTags: ['厚乳', '可可', '暖香'], averageRating: 4.7, ratingCount: 84, triedCount: 590, favoriteCount: 162, forkCount: 54, hotScore: 1320 },
  { id: 2004, recipeName: '椰云美式', name: '椰云美式', authorName: 'Island Bean', cupType: 'coldCup', temperatureType: 'cold', flavorTags: ['椰香', '轻盈', '低糖'], averageRating: 4.6, ratingCount: 70, triedCount: 410, favoriteCount: 126, forkCount: 41, hotScore: 1040 }
]

export const myRecipes = [
  { id: 1001, name: '周末焦糖燕麦拿铁', cupType: 'coldCup', temperatureType: 'cold', flavorTags: ['焦糖', '燕麦奶', '半糖'], isPublic: true, createdAt: '2026-05-09 10:00:00' },
  { id: 1002, name: '热厚乳澳白', cupType: 'hotCup', temperatureType: 'hot', flavorTags: ['厚乳', '高因', '温热'], isPublic: false, createdAt: '2026-05-09 10:20:00' }
]
